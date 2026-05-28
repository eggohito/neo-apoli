package io.github.eggohito.neo_apoli.condition.manager;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@ApiStatus.NonExtendable
public class ConditionManager {

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

	public record ClientboundConditionsUpdatePacket(Map<ResourceLocation, Condition> conditions) implements CustomPacketPayload {

		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Condition>> CONDITIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, Condition.STREAM_CODEC);

		public static final Type<ClientboundConditionsUpdatePacket> TYPE = new Type<>(NeoApoli.id("clientbound/update_conditions"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundConditionsUpdatePacket> CODEC = CONDITIONS_CODEC.map(ClientboundConditionsUpdatePacket::new, ClientboundConditionsUpdatePacket::conditions);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle() {
			ConditionManager.conditions = ImmutableMap.copyOf(conditions());
		}

	}

}
