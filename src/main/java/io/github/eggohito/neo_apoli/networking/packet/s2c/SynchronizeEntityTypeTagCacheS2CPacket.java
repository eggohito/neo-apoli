package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.EntityType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.tag.TagKey;

import java.util.Map;
import java.util.Set;

public record SynchronizeEntityTypeTagCacheS2CPacket(Map<TagKey<EntityType<?>>, Set<TagKey<EntityType<?>>>> tags) implements CustomPayload {

	private static final PacketCodec<ByteBuf, Map<TagKey<EntityType<?>>, Set<TagKey<EntityType<?>>>>> TAGS_CODEC = PacketCodecs.map(Object2ObjectOpenHashMap::new, NeoApoliPacketCodecs.ENTITY_TYPE_TAG, NeoApoliPacketCodecs.ENTITY_TYPE_TAG_SET);

	public static final Id<SynchronizeEntityTypeTagCacheS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_tag_cache"));
	public static final PacketCodec<ByteBuf, SynchronizeEntityTypeTagCacheS2CPacket> CODEC = TAGS_CODEC.xmap(SynchronizeEntityTypeTagCacheS2CPacket::new, SynchronizeEntityTypeTagCacheS2CPacket::tags);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
