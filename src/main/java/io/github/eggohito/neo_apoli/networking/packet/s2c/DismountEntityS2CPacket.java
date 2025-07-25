package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record DismountEntityS2CPacket(int passengerId) implements CustomPayload {

	public static final Id<DismountEntityS2CPacket> ID = new Id<>(NeoApoli.id("s2c/dismount_entity"));
	public static final PacketCodec<ByteBuf, DismountEntityS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.INTEGER, DismountEntityS2CPacket::passengerId,
		DismountEntityS2CPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
