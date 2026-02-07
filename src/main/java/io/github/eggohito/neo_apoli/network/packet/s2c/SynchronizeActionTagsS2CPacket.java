package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record SynchronizeActionTagsS2CPacket(Map<ResourceLocation, List<Action>> tags) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, List<Action>>> TAGS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.collection(ObjectArrayList::new, Action.STREAM_CODEC));

	public static final Type<SynchronizeActionTagsS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_action_tags"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeActionTagsS2CPacket> CODEC = TAGS_CODEC.map(SynchronizeActionTagsS2CPacket::new, SynchronizeActionTagsS2CPacket::tags);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
