package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public record SynchronizeEntityTypeTagCacheS2CPacket(Map<TagKey<EntityType<?>>, Set<TagKey<EntityType<?>>>> tags) implements CustomPacketPayload {

	private static final StreamCodec<ByteBuf, Map<TagKey<EntityType<?>>, Set<TagKey<EntityType<?>>>>> TAGS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, NeoApoliStreamCodecs.ENTITY_TYPE_TAG, NeoApoliStreamCodecs.ENTITY_TYPE_TAG_SET);

	public static final Type<SynchronizeEntityTypeTagCacheS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_tag_cache"));
	public static final StreamCodec<ByteBuf, SynchronizeEntityTypeTagCacheS2CPacket> CODEC = TAGS_CODEC.map(SynchronizeEntityTypeTagCacheS2CPacket::new, SynchronizeEntityTypeTagCacheS2CPacket::tags);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
