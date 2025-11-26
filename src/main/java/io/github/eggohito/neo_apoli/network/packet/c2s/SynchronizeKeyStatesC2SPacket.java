package io.github.eggohito.neo_apoli.network.packet.c2s;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record SynchronizeKeyStatesC2SPacket(Object2BooleanMap<String> states) implements CustomPacketPayload {

	private static final StreamCodec<ByteBuf, Object2BooleanMap<String>> STATES_CODEC = ByteBufCodecs.map(Object2BooleanOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.BOOL);

	public static final Type<SynchronizeKeyStatesC2SPacket> TYPE = new Type<>(NeoApoli.id("c2s/synchronize_key_states"));
	public static final StreamCodec<ByteBuf, SynchronizeKeyStatesC2SPacket> CODEC = STATES_CODEC.map(SynchronizeKeyStatesC2SPacket::new, SynchronizeKeyStatesC2SPacket::states);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
