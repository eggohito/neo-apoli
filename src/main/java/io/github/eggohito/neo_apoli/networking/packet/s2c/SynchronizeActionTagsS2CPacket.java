package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public record SynchronizeActionTagsS2CPacket(Map<Identifier, List<Action>> tags) implements CustomPayload {

	private static final PacketCodec<RegistryByteBuf, Map<Identifier, List<Action>>> TAGS_PACKET_CODEC = PacketCodecs.map(Object2ObjectOpenHashMap::new, Identifier.PACKET_CODEC, PacketCodecs.collection(ObjectArrayList::new, Action.PACKET_CODEC));

	public static final Id<SynchronizeActionTagsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_action_tags"));
	public static final PacketCodec<RegistryByteBuf, SynchronizeActionTagsS2CPacket> CODEC = TAGS_PACKET_CODEC.xmap(SynchronizeActionTagsS2CPacket::new, SynchronizeActionTagsS2CPacket::tags);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
