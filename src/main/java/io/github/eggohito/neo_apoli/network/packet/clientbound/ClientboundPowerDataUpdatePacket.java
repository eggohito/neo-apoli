package io.github.eggohito.neo_apoli.network.packet.clientbound;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

public record ClientboundPowerDataUpdatePacket(int entityId, Map<PowerIdentifier, Dynamic<?>> powersAndData) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<PowerIdentifier, Dynamic<?>>> POWERS_AND_DATA_CODEC = ByteBufCodecs.map(
		Object2ObjectOpenHashMap::new,
		PowerIdentifier.STREAM_CODEC,
		NeoApoliStreamCodecs.REGISTRY_PASSTHROUGH
	);

	public static final Type<ClientboundPowerDataUpdatePacket> TYPE = new Type<>(NeoApoli.id("clientbound/update_power_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPowerDataUpdatePacket> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundPowerDataUpdatePacket::entityId, POWERS_AND_DATA_CODEC, ClientboundPowerDataUpdatePacket::powersAndData, ClientboundPowerDataUpdatePacket::new);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(Level level) {

		Entity entity = level.getEntity(this.entityId());

		if (entity == null) {
			NeoApoli.LOGGER.warn("Couldn't sync data of the following powers to non-existent entity: [{}]", powersAndData().keySet().stream().map(PowerIdentifier::toString).collect(Collectors.joining(", ")));
		}

		else {

			for (var entry : powersAndData().entrySet()) {

				PowerIdentifier id = entry.getKey();
				Dynamic<?> data = entry.getValue();

				if (!PowerManager.getInstance().contains(id)) {
					NeoApoli.LOGGER.warn("Couldn't sync data of unregistered {}!", id.asDisplayString(false));
				}

				else {

					Powers powers = Powers.getOrCreate(entity);
					RegistryOps<Tag> nbtOps = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);

					if (powers.hasInstance(id)) {

						Power.Instance<?> instance = powers.getInstance(id);
						Tag nbtData = data.convert(nbtOps).getValue();

						if (instance.decodeData(nbtOps, nbtData) instanceof DataResult.Error<Unit> error) {
							NeoApoli.LOGGER.warn("Couldn't decode synced data of {} to entity {}: {}", id.asDisplayString(false), entity.getName().getString(), error.message());
						}

					}

					else {
						NeoApoli.LOGGER.warn("Couldn't sync data of {} to entity {} as it wasn't granted!", id.asDisplayString(false), entity.getName().getString());
					}

				}

			}

		}

	}

	public static <T> ClientboundPowerDataUpdatePacket single(int entityId, DynamicOps<T> ops, PowerIdentifier id, T data) {
		return bulk(entityId, ops, Map.of(id, data));
	}

	public static <T> ClientboundPowerDataUpdatePacket bulk(int entityId, DynamicOps<T> ops, Map<PowerIdentifier, T> powersAndData) {

		Map<PowerIdentifier, Dynamic<?>> dynamicMap = new Object2ObjectOpenHashMap<>();
		powersAndData.forEach((reference, t) -> dynamicMap.put(reference, new Dynamic<>(ops, t)));

		return new ClientboundPowerDataUpdatePacket(entityId, dynamicMap);

	}

}
