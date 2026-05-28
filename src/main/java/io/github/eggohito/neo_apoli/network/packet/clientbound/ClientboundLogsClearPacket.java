package io.github.eggohito.neo_apoli.network.packet.clientbound;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public final class ClientboundLogsClearPacket implements CustomPacketPayload {

	public static final ClientboundLogsClearPacket INSTANCE = new ClientboundLogsClearPacket();

	public static final Type<ClientboundLogsClearPacket> TYPE = new Type<>(NeoApoli.id("clientbound/clear_logs"));
	public static final StreamCodec<ByteBuf, ClientboundLogsClearPacket> CODEC = StreamCodec.unit(INSTANCE);

	private ClientboundLogsClearPacket() {

	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
