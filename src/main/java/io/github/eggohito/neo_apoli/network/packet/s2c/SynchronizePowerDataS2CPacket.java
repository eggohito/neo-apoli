package io.github.eggohito.neo_apoli.network.packet.s2c;

import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Map;

public record SynchronizePowerDataS2CPacket(int entityId, Map<PowerReference, Dynamic<?>> powersAndData) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<PowerReference, Dynamic<?>>> POWERS_AND_DATA_CODEC = ByteBufCodecs.map(
		Object2ObjectOpenHashMap::new,
		PowerReference.STREAM_CODEC,
		NeoApoliStreamCodecs.REGISTRY_PASSTHROUGH
	);

	public static final Type<SynchronizePowerDataS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_power_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizePowerDataS2CPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, SynchronizePowerDataS2CPacket::entityId,
		POWERS_AND_DATA_CODEC, SynchronizePowerDataS2CPacket::powersAndData,
		SynchronizePowerDataS2CPacket::new
	);

	public SynchronizePowerDataS2CPacket(int entityId, PowerReference reference, Dynamic<?> data) {
		this(entityId, Map.of(reference, data));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
