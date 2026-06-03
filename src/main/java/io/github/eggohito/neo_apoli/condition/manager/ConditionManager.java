package io.github.eggohito.neo_apoli.condition.manager;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public class ConditionManager {

	private static final StreamCodec<ByteBuf, Map<ResourceLocation, Tag>> CONDITIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.TRUSTED_TAG);
	public static final ResourceLocation ID = NeoApoli.id("manager/condition");

	protected static volatile ImmutableMap<ResourceLocation, Condition> conditions = ImmutableMap.of();

	public ConditionManager() {
		//  Disallow extending non-internal classes
		var ignored = (ServerConditionManager) this;
	}

	public static DataResult<Condition> getAsResult(ResourceLocation id) {
		var candidate = conditions.get(id);
		return candidate != null
			? DataResult.success(candidate)
			: DataResult.error(() -> "Unknown condition: \"" + id + "\"");
	}

	public static Condition get(ResourceLocation id) {
		return getAsResult(id).getOrThrow();
	}

	public static DataResult<ResourceLocation> getIdAsResult(Condition condition) {

		for (var candidate : conditions.entrySet()) {

			if (candidate.getValue() == condition) {
				return DataResult.success(candidate.getKey());
			}

		}

		return DataResult.error(() -> "No ID found for " + condition);

	}

	public static ResourceLocation getId(Condition condition) {
		return getIdAsResult(condition).getOrThrow();
	}

	public static Iterable<ResourceLocation> ids() {
		return conditions.keySet();
	}

	public static Iterable<Condition> conditions() {
		return conditions.values();
	}

	public static boolean contains(ResourceLocation id) {
		return getAsResult(id).isSuccess();
	}

	public static boolean containsId(Condition condition) {
		return getIdAsResult(condition).isSuccess();
	}

	protected static void send(RegistryAccess registryAccess, Consumer<CustomPacketPayload> sender) {

		Map<ResourceLocation, Tag> conditions = new Object2ObjectLinkedOpenHashMap<>();
		RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);

		for (var entry : ConditionManager.conditions.entrySet()) {

			ResourceLocation id = entry.getKey();
			Condition condition = entry.getValue();

			Condition.CODEC.encodeStart(ops, condition)
				.ifError(error -> NeoApoli.LOGGER.error("Couldn't encode condition \"{}\" during the syncing process (skipping): {}", id, error.message()))
				.ifSuccess(tag -> conditions.put(id, tag));

		}

		sender.accept(new ClientboundUpdateConditionsPacket(conditions));

	}

	protected static ImmutableMap<ResourceLocation, Condition> unpackConditions(RegistryAccess registryAccess, Map<ResourceLocation, Tag> packed) {

		ImmutableMap.Builder<ResourceLocation, Condition> builder = ImmutableMap.builder();
		RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);

		for (var entry : packed.entrySet()) {

			ResourceLocation id = entry.getKey();
			Tag tag = entry.getValue();

			Condition.CODEC.parse(ops, tag)
				.ifError(error -> NeoApoli.LOGGER.error("Couldn't receive condition \"{}\" from the server: {}", id, error.message()))
				.ifSuccess(condition -> builder.put(id, condition));

		}

		return builder.build();

	}

	public record SynchronizeTask(RegistryAccess registryAccess) implements ConfigurationTask {

		public static final Type TYPE = new Type(NeoApoli.id("task/synchronize_conditions").toString());

		@Override
		public void start(Consumer<Packet<?>> task) {
			send(registryAccess(), packet -> task.accept(ServerConfigurationNetworking.createS2CPacket(packet)));
		}

		@Override
		public @NotNull Type type() {
			return TYPE;
		}

	}

	public enum ServerboundSyncAcknowledgedPacket implements CustomPacketPayload {

		INSTANCE;

		public static final Type<ServerboundSyncAcknowledgedPacket> TYPE = new Type<>(NeoApoli.id("serverbound/conditions/sync_acknowledged"));
		public static final StreamCodec<ByteBuf, ServerboundSyncAcknowledgedPacket> CODEC = StreamCodec.unit(INSTANCE);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(ServerConfigurationNetworking.Context context) {
			context.networkHandler().completeTask(SynchronizeTask.TYPE);
		}

	}

	public record ClientboundUpdateConditionsPacket(Map<ResourceLocation, Tag> conditions) implements CustomPacketPayload {

		public static final Type<ClientboundUpdateConditionsPacket> TYPE = new Type<>(NeoApoli.id("clientbound/update_conditions"));
		public static final StreamCodec<ByteBuf, ClientboundUpdateConditionsPacket> CODEC = CONDITIONS_CODEC.map(ClientboundUpdateConditionsPacket::new, ClientboundUpdateConditionsPacket::conditions);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(RegistryAccess registryAccess) {
			ConditionManager.conditions = unpackConditions(registryAccess, conditions());
		}

	}

}
