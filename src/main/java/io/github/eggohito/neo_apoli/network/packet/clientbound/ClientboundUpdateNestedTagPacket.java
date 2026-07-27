package io.github.eggohito.neo_apoli.network.packet.clientbound;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.tag.NestedTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundUpdateNestedTagPacket<T>(NestedTag<T> nestedTag) implements CustomPacketPayload {

	public static final Type<ClientboundUpdateNestedTagPacket<?>> TYPE = new Type<>(NeoApoli.id("clientbound/nested_tag_cache/update"));
	public static final StreamCodec<FriendlyByteBuf, ClientboundUpdateNestedTagPacket<?>> CODEC = NestedTag.STREAM_CODEC.map(ClientboundUpdateNestedTagPacket::new, ClientboundUpdateNestedTagPacket::nestedTag);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
