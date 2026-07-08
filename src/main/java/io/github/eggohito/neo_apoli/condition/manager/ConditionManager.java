package io.github.eggohito.neo_apoli.condition.manager;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

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

	protected static void send(Consumer<CustomPacketPayload> sender) {
		sender.accept(new ClientboundUpdatePacket(conditions));
	}

	public record ClientboundUpdatePacket(Map<ResourceLocation, Condition> conditions) implements CustomPacketPayload {

		public static final Type<ClientboundUpdatePacket> TYPE = new Type<>(ID.withPath(path -> "clientbound/" + path + "/update"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdatePacket> CODEC = StreamCodec.ofMember(ClientboundUpdatePacket::send, ClientboundUpdatePacket::receive);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		private static ClientboundUpdatePacket receive(RegistryFriendlyByteBuf buf) {
			
			Map<ResourceLocation, Condition> conditions = new Object2ObjectLinkedOpenHashMap<>();
			int count = buf.readInt();

			for (int i = 0; i < count; i++) {

				ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);

				try {
					conditions.put(id, Condition.STREAM_CODEC.decode(buf));
				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't decode condition \"{}\" during the syncing process", id, e);
					throw e;
				}
				
			}

			return new ClientboundUpdatePacket(conditions);
			
		}

		private void send(RegistryFriendlyByteBuf buf) {

			buf.writeInt(conditions().size());
			
			for (var entry : conditions().entrySet()) {
				
				ResourceLocation.STREAM_CODEC.encode(buf, entry.getKey());
				
				try {
					Condition.STREAM_CODEC.encode(buf, entry.getValue());
				}
				
				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't encode condition \"{}\" during the syncing process", entry.getKey(), e);
					throw e;
				}
				
			}
			
		}
		

	}

}
