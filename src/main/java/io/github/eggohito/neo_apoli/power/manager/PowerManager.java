package io.github.eggohito.neo_apoli.power.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public class PowerManager {

	private static final StreamCodec<ByteBuf, Map<PowerIdentifier, Tag>> POWERS_STREAM_CODEC = ByteBufCodecs.map(Object2ObjectLinkedOpenHashMap::new, PowerIdentifier.STREAM_CODEC, ByteBufCodecs.TRUSTED_TAG);
	private static final StreamCodec<ByteBuf, Map<ResourceLocation, List<PowerIdentifier>>> TAGS_STREAM_CODEC = ByteBufCodecs.map(Object2ObjectLinkedOpenHashMap::new, ResourceLocation.STREAM_CODEC, PowerIdentifier.STREAM_CODEC.apply(ByteBufCodecs.list()));

	public static final ResourceLocation ID = NeoApoli.id("manager/power");
	public static final TagEntry.Lookup<PowerHolder<?>> TAG_LOOKUP = new TagEntry.Lookup<>() {

		@Override
		public @Nullable PowerHolder<?> element(ResourceLocation id, boolean required) {
			return getAsResult(PowerIdentifier.of(id)).result().orElse(null);
		}

		@Override
		public @Nullable Collection<PowerHolder<?>> tag(ResourceLocation id) {
			return getTag(id).result().orElse(null);
		}

		@Override
		public String toString() {
			return "Power manager";
		}

	};

	protected static volatile ImmutableMap<PowerIdentifier, PowerHolder<?>> powers = ImmutableMap.of();
	protected static volatile ImmutableMap<ResourceLocation, List<PowerHolder<?>>> tags = ImmutableMap.of();

	public PowerManager() {
		//  Disallow extending non-internal classes
		var ignored = (ServerPowerManager) this;
	}

	public static DataResult<PowerHolder<?>> getAsResult(PowerIdentifier id) {
		var candidate = powers.get(id);
		return candidate != null
			? DataResult.success(candidate)
			: DataResult.error(() -> "Unknown " + id.asDisplayString(false));
	}

	public static PowerHolder<?> get(PowerIdentifier id) {
		return getAsResult(id).getOrThrow();
	}

	public static DataResult<PowerIdentifier> getIdAsResult(Power power) {

		for (var candidate : powers.values()) {

			if (candidate.value() == power) {
				return DataResult.success(candidate.id());
			}

		}

		return DataResult.error(() -> "No ID found for " + power);

	}

	public static PowerIdentifier getId(Power power) {
		return getIdAsResult(power).getOrThrow();
	}

	public static DataResult<List<PowerHolder<?>>> getTag(ResourceLocation id) {
		var candidates = tags.get(id);
		return candidates != null
			? DataResult.success(candidates)
			: DataResult.error(() -> "Unknown power tag: \"" + id + "\"");
	}

	public static List<PowerHolder<?>> getTagOrEmpty(ResourceLocation id) {
		return getTag(id)
			.result()
			.orElseGet(List::of);
	}

	public static Iterable<PowerIdentifier> ids() {
		return powers.keySet();
	}

	public static Iterable<PowerHolder<?>> powers() {
		return powers.values();
	}

	public static Iterable<ResourceLocation> tags() {
		return tags.keySet();
	}

	public static boolean contains(PowerIdentifier id) {
		return getAsResult(id).isSuccess();
	}

	public static boolean containsId(Power power) {
		return getIdAsResult(power).isSuccess();
	}

	protected static void register(BiConsumer<PowerIdentifier, PowerHolder<?>> builder, PowerHolder<?> powerHolder) {

		builder.accept(powerHolder.id(), powerHolder);

		if (powerHolder.value() instanceof MultiplePower multiplePower) {

			if (powerHolder.id().isSubPower()) {
				throw new IllegalStateException("Tried to register " + powerHolder.id().asDisplayString(false) + " with \"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, multiplePower.getType()) + "\" power type, which is not allowed!");
			}

			else {
				multiplePower.getSubPowers().forEach(subPowerHolder -> register(builder, subPowerHolder));
			}

		}

	}

	public record SynchronizeTask(Map<PowerIdentifier, Tag> powers, Map<ResourceLocation, List<PowerIdentifier>> tags) implements ConfigurationTask {

		public static final Type TYPE = new Type(NeoApoli.id("task/synchronize_powers").toString());

		@Override
		public void start(Consumer<Packet<?>> task) {
			task.accept(ServerConfigurationNetworking.createS2CPacket(new ClientboundUpdatePowersPacket(powers())));
			task.accept(ServerConfigurationNetworking.createS2CPacket(new ClientboundUpdateTagsPacket(tags())));
			task.accept(ServerConfigurationNetworking.createS2CPacket(ClientboundSyncInitiatedPacket.INSTANCE));
		}

		@Override
		public @NotNull Type type() {
			return TYPE;
		}

	}

	public enum ServerboundSyncAcknowledgedPacket implements CustomPacketPayload {

		INSTANCE;

		public static final Type<ServerboundSyncAcknowledgedPacket> TYPE = new Type<>(NeoApoli.id("serverbound/powers/sync_acknowledged"));
		public static final StreamCodec<ByteBuf, ServerboundSyncAcknowledgedPacket> CODEC = StreamCodec.unit(INSTANCE);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(ServerConfigurationNetworking.Context context) {
			context.networkHandler().completeTask(SynchronizeTask.TYPE);
		}

	}

	public enum ClientboundSyncInitiatedPacket implements CustomPacketPayload {

		INSTANCE;

		public static final Type<ClientboundSyncInitiatedPacket> TYPE = new Type<>(NeoApoli.id("clientbound/powers/sync_initiated"));
		public static final StreamCodec<ByteBuf, ClientboundSyncInitiatedPacket> CODEC = StreamCodec.unit(INSTANCE);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(PacketSender sender) {
			sender.sendPacket(ServerboundSyncAcknowledgedPacket.INSTANCE);
		}

	}

	public record ClientboundUpdatePowersPacket(Map<PowerIdentifier, Tag> powers) implements CustomPacketPayload {

		public static final Type<ClientboundUpdatePowersPacket> TYPE = new Type<>(NeoApoli.id("clientbound/powers/update_powers"));
		public static final StreamCodec<ByteBuf, ClientboundUpdatePowersPacket> CODEC = POWERS_STREAM_CODEC.map(ClientboundUpdatePowersPacket::new, ClientboundUpdatePowersPacket::powers);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(RegistryAccess registryAccess) {

			ImmutableMap.Builder<PowerIdentifier, PowerHolder<?>> builder = ImmutableMap.builder();
			RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);

			for (var entry : powers().entrySet()) {

				PowerIdentifier id = entry.getKey();
				Tag tag = entry.getValue();

				PowerHolder.CODEC.parse(ops, tag)
					.ifError(error -> NeoApoli.LOGGER.error("Couldn't receive {} from the server: {}", id.asDisplayString(false), error.message()))
					.ifSuccess(holder -> register(builder::put, holder));

			}

			PowerManager.powers = builder.build();

		}

	}

	public record ClientboundUpdateTagsPacket(Map<ResourceLocation, List<PowerIdentifier>> tags) implements CustomPacketPayload {

		public static final Type<ClientboundUpdateTagsPacket> TYPE = new Type<>(NeoApoli.id("clientbound/powers/update_tags"));
		public static final StreamCodec<ByteBuf, ClientboundUpdateTagsPacket> CODEC = TAGS_STREAM_CODEC.map(ClientboundUpdateTagsPacket::new, ClientboundUpdateTagsPacket::tags);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle() {

			ImmutableMap.Builder<ResourceLocation, List<PowerHolder<?>>> tagsBuilder = ImmutableMap.builder();
			for (var entry : tags.entrySet()) {

				ResourceLocation tagId = entry.getKey();
				List<PowerIdentifier> powerIds = entry.getValue();

				ImmutableList.Builder<PowerHolder<?>> powersBuilder = ImmutableList.builder();
				Reporter reporter = new Reporter("{\"#" + tagId + "\"}");

				for (var powerId : powerIds) {
					PowerManager.getAsResult(powerId)
						.ifError(error -> reporter.forChild(".\"" + powerId + "\"").report(error.message()))
						.ifSuccess(powersBuilder::add);
				}

				reporter.getErrorsFlattened().ifPresent(errors -> NeoApoli.LOGGER.error("Couldn't properly receive power tag \"{}\" due to errors {}", tagId, errors));
				tagsBuilder.put(tagId, powersBuilder.build());

			}

			PowerManager.tags = tagsBuilder.build();

		}

	}

}
