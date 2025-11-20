package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;

public record SynchronizeActionsS2CPacket(Map<Identifier, Action> actions) implements CustomPayload {

	private static final PacketCodec<RegistryByteBuf, Map<Identifier, Action>> ACTIONS_PACKET_CODEC = PacketCodecs.map(Object2ObjectOpenHashMap::new, Identifier.PACKET_CODEC, Action.PACKET_CODEC);

	public static final Id<SynchronizeActionsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_actions"));
	public static final PacketCodec<RegistryByteBuf, SynchronizeActionsS2CPacket> CODEC = ACTIONS_PACKET_CODEC.xmap(SynchronizeActionsS2CPacket::new, SynchronizeActionsS2CPacket::actions);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
