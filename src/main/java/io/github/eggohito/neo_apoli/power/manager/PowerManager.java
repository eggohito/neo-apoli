package io.github.eggohito.neo_apoli.power.manager;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

@ApiStatus.NonExtendable
public class PowerManager {

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

	public record ClientboundPowersUpdatePacket(Set<PowerHolder<?>> powers) implements CustomPacketPayload {

		public static final Type<ClientboundPowersUpdatePacket> TYPE = new Type<>(NeoApoli.id("clientbound/update_powers"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPowersUpdatePacket> CODEC = PowerHolder.STREAM_CODEC.apply(StreamCodecUtil.set()).map(ClientboundPowersUpdatePacket::new, ClientboundPowersUpdatePacket::powers);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle() {

			ImmutableMap.Builder<PowerIdentifier, PowerHolder<?>> builder = ImmutableMap.builder();
			powers().forEach(power -> register(builder::put, power));

			PowerManager.powers = builder.build();

		}

	}

	public record ClientboundTagsUpdatePacket(Map<ResourceLocation, List<PowerIdentifier>> tags) implements CustomPacketPayload {

		private static final StreamCodec<ByteBuf, List<PowerIdentifier>> IDS_CODEC = PowerIdentifier.STREAM_CODEC.apply(ByteBufCodecs.list());
		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, List<PowerIdentifier>>> TAGS_CODEC = ByteBufCodecs.map(Object2ObjectLinkedOpenHashMap::new, ResourceLocation.STREAM_CODEC, IDS_CODEC);

		public static final Type<ClientboundTagsUpdatePacket> TYPE = new Type<>(NeoApoli.id("clientbound/update_power_tags"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTagsUpdatePacket> CODEC = TAGS_CODEC.map(ClientboundTagsUpdatePacket::new, ClientboundTagsUpdatePacket::tags);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle() {

			Map<ResourceLocation, List<PowerHolder<?>>> tags = new Object2ObjectLinkedOpenHashMap<>();
			for (var entry : tags().entrySet()) {

				ResourceLocation tagId = entry.getKey();
				List<PowerIdentifier> powerIds = entry.getValue();

				for (var powerId : powerIds) {
					PowerManager.getAsResult(powerId)
						.ifSuccess(power -> tags.computeIfAbsent(tagId, k -> new ObjectArrayList<>()).add(power))
						.ifError(error -> NeoApoli.LOGGER.error("Couldn't properly receive power tag \"{}\": {}", tagId, error.message()));
				}

			}

			PowerManager.tags = ImmutableMap.copyOf(tags);

		}

	}

}
