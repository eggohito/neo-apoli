package io.github.eggohito.neo_apoli.network.packet.clientbound;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public enum ClientboundClearCachedLogsPacket implements CustomPacketPayload {

	INSTANCE;

	public static final Type<ClientboundClearCachedLogsPacket> TYPE = new Type<>(NeoApoli.id("clientbound/clear_logs"));
	public static final StreamCodec<ByteBuf, ClientboundClearCachedLogsPacket> CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
