package io.github.eggohito.neo_apoli.network.packet.c2s;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RequestActionTagsC2SPacket() implements CustomPacketPayload {

	public static final Type<RequestActionTagsC2SPacket> TYPE = new Type<>(NeoApoli.id("c2s/request_action_tags"));
	public static final StreamCodec<ByteBuf, RequestActionTagsC2SPacket> CODEC = StreamCodec.unit(new RequestActionTagsC2SPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
