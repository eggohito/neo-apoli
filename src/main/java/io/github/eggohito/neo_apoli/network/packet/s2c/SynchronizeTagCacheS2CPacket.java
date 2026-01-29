package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Map;
import java.util.Set;

public record SynchronizeTagCacheS2CPacket<T>(ResourceKey<? extends Registry<T>> registry, Map<TagKey<T>, Set<TagKey<T>>> cache) implements CustomPacketPayload {

	public static final Type<SynchronizeTagCacheS2CPacket<?>> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_tag_cache"));
	public static final StreamCodec<FriendlyByteBuf, SynchronizeTagCacheS2CPacket<?>> CODEC = StreamCodec.ofMember(SynchronizeTagCacheS2CPacket::encode, SynchronizeTagCacheS2CPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	private static <T> SynchronizeTagCacheS2CPacket<T> decode(FriendlyByteBuf buf) {

		ResourceKey<? extends Registry<T>> registry = buf.readRegistryKey();
		Map<TagKey<T>, Set<TagKey<T>>> cache = createCacheCodec(registry).decode(buf);

		return new SynchronizeTagCacheS2CPacket<>(registry, cache);

	}

	private void encode(FriendlyByteBuf buf) {
		buf.writeResourceKey(this.registry());
		createCacheCodec(this.registry()).encode(buf, this.cache());
	}

	private static <T, B extends ByteBuf> StreamCodec<B, Map<TagKey<T>, Set<TagKey<T>>>> createCacheCodec(ResourceKey<? extends Registry<T>> registry) {

		StreamCodec<B, TagKey<T>> singleCodec = TagKey.streamCodec(registry).cast();
		StreamCodec<B, Set<TagKey<T>>> multipleCodec = ByteBufCodecs.collection(ObjectOpenHashSet::new, singleCodec);

		return ByteBufCodecs.map(Object2ObjectOpenHashMap::new, singleCodec, multipleCodec);

	}

}
