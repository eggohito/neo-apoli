package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public final class ClearLogsS2CPacket implements CustomPayload {

	public static final ClearLogsS2CPacket INSTANCE = new ClearLogsS2CPacket();

	public static final Id<ClearLogsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/clear_logs"));
	public static final PacketCodec<ByteBuf, ClearLogsS2CPacket> CODEC = PacketCodec.unit(INSTANCE);

	private ClearLogsS2CPacket() {

	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
