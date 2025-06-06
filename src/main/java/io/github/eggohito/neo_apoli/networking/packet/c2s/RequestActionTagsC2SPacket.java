package io.github.eggohito.neo_apoli.networking.packet.c2s;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record RequestActionTagsC2SPacket() implements CustomPayload {

	public static final Id<RequestActionTagsC2SPacket> ID = new Id<>(NeoApoli.id("c2s/request_action_tags"));
	public static final PacketCodec<ByteBuf, RequestActionTagsC2SPacket> CODEC = PacketCodec.unit(new RequestActionTagsC2SPacket());

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
