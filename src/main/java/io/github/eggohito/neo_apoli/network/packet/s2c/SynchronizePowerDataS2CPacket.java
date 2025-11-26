package io.github.eggohito.neo_apoli.network.packet.s2c;

import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SynchronizePowerDataS2CPacket(int entityId, PowerReference powerReference, Dynamic<?> data) implements CustomPacketPayload {

	public static final Type<SynchronizePowerDataS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_power_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizePowerDataS2CPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, SynchronizePowerDataS2CPacket::entityId,
		PowerReference.STREAM_CODEC, SynchronizePowerDataS2CPacket::powerReference,
		NeoApoliStreamCodecs.REGISTRY_PASSTHROUGH, SynchronizePowerDataS2CPacket::data,
		SynchronizePowerDataS2CPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
