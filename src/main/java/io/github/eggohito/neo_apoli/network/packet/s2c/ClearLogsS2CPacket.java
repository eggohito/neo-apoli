package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class ClearLogsS2CPacket implements CustomPacketPayload {

	public static final ClearLogsS2CPacket INSTANCE = new ClearLogsS2CPacket();

	public static final Type<ClearLogsS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/clear_logs"));
	public static final StreamCodec<ByteBuf, ClearLogsS2CPacket> CODEC = StreamCodec.unit(INSTANCE);

	private ClearLogsS2CPacket() {

	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
