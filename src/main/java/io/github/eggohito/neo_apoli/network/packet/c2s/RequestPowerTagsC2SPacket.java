package io.github.eggohito.neo_apoli.network.packet.c2s;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RequestPowerTagsC2SPacket() implements CustomPacketPayload {

	public static final Type<RequestPowerTagsC2SPacket> TYPE = new Type<>(NeoApoli.id("c2s/request_power_tags"));
	public static final StreamCodec<ByteBuf, RequestPowerTagsC2SPacket> CODEC = StreamCodec.unit(new RequestPowerTagsC2SPacket());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
