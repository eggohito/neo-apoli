package io.github.eggohito.neo_apoli.networking.packet.c2s;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record RequestPowerTagsC2SPacket() implements CustomPayload {

	public static final Id<RequestPowerTagsC2SPacket> ID = new Id<>(NeoApoli.id("c2s/request_power_tags"));
	public static final PacketCodec<ByteBuf, RequestPowerTagsC2SPacket> CODEC = PacketCodec.unit(new RequestPowerTagsC2SPacket());

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
