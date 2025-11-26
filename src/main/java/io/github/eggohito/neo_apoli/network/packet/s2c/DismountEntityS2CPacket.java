package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DismountEntityS2CPacket(int passengerId) implements CustomPacketPayload {

	public static final Type<DismountEntityS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/dismount_entity"));
	public static final StreamCodec<ByteBuf, DismountEntityS2CPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, DismountEntityS2CPacket::passengerId,
		DismountEntityS2CPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
