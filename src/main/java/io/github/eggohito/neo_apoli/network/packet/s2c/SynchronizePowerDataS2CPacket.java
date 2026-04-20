package io.github.eggohito.neo_apoli.network.packet.s2c;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Map;

public record SynchronizePowerDataS2CPacket(int entityId, Map<PowerIdentifier, Dynamic<?>> powersAndData) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<PowerIdentifier, Dynamic<?>>> POWERS_AND_DATA_CODEC = ByteBufCodecs.map(
		Object2ObjectOpenHashMap::new,
		PowerIdentifier.STREAM_CODEC,
		NeoApoliStreamCodecs.REGISTRY_PASSTHROUGH
	);

	public static final Type<SynchronizePowerDataS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_power_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizePowerDataS2CPacket> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, SynchronizePowerDataS2CPacket::entityId, POWERS_AND_DATA_CODEC, SynchronizePowerDataS2CPacket::powersAndData, SynchronizePowerDataS2CPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static <T> SynchronizePowerDataS2CPacket single(int entityId, DynamicOps<T> ops, PowerIdentifier id, T data) {
		return bulk(entityId, ops, Map.of(id, data));
	}

	public static <T> SynchronizePowerDataS2CPacket bulk(int entityId, DynamicOps<T> ops, Map<PowerIdentifier, T> powersAndData) {

		Map<PowerIdentifier, Dynamic<?>> dynamicMap = new Object2ObjectOpenHashMap<>();
		powersAndData.forEach((reference, t) -> dynamicMap.put(reference, new Dynamic<>(ops, t)));

		return new SynchronizePowerDataS2CPacket(entityId, dynamicMap);

	}

}
