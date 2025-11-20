package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;

public record SynchronizeConditionsS2CPacket(Map<Identifier, Condition> conditions) implements CustomPayload {

	private static final PacketCodec<RegistryByteBuf, Map<Identifier, Condition>> CONDITIONS_PACKET_CODEC = PacketCodecs.map(Object2ObjectOpenHashMap::new, Identifier.PACKET_CODEC, Condition.PACKET_CODEC);

	public static final Id<SynchronizeConditionsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_conditions"));
	public static final PacketCodec<RegistryByteBuf, SynchronizeConditionsS2CPacket> CODEC = CONDITIONS_PACKET_CODEC.xmap(SynchronizeConditionsS2CPacket::new, SynchronizeConditionsS2CPacket::conditions);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
