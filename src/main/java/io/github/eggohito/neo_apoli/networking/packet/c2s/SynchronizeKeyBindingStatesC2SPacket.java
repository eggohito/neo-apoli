package io.github.eggohito.neo_apoli.networking.packet.c2s;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SynchronizeKeyBindingStatesC2SPacket(Object2BooleanMap<String> states) implements CustomPayload {

	private static final PacketCodec<ByteBuf, Object2BooleanMap<String>> STATES_CODEC = PacketCodecs.map(Object2BooleanOpenHashMap::new, PacketCodecs.STRING, PacketCodecs.BOOLEAN);

	public static final Id<SynchronizeKeyBindingStatesC2SPacket> ID = new Id<>(NeoApoli.id("c2s/synchronize_keybinding_states"));
	public static final PacketCodec<ByteBuf, SynchronizeKeyBindingStatesC2SPacket> CODEC = STATES_CODEC.xmap(SynchronizeKeyBindingStatesC2SPacket::new, SynchronizeKeyBindingStatesC2SPacket::states);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
