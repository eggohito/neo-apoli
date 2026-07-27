package io.github.eggohito.neo_apoli.tag;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public record NestedTag<T>(ResourceKey<? extends Registry<T>> registryKey, ImmutableSetMultimap<TagKey<T>, TagKey<T>> values) {

	public static final StreamCodec<FriendlyByteBuf, NestedTag<?>> STREAM_CODEC = StreamCodec.ofMember(NestedTag::send, NestedTag::receive);

	public Set<TagKey<T>> getOrEmpty(TagKey<T> tag) {
		return values().get(tag);
	}

	private void send(FriendlyByteBuf buf) {
		buf.writeResourceKey(this.registryKey());
		streamCodecMultimap(this.registryKey()).encode(buf, this.values());
	}

	private static <T> NestedTag<T> receive(FriendlyByteBuf buf) {

		ResourceKey<? extends Registry<T>> registryKey = buf.readRegistryKey();
		StreamCodec<ByteBuf, ImmutableSetMultimap<TagKey<T>, TagKey<T>>> codecMultimap = streamCodecMultimap(registryKey);

		return new NestedTag<>(registryKey, codecMultimap.decode(buf));

	}

	private static <T> StreamCodec<ByteBuf, ImmutableSetMultimap<TagKey<T>, TagKey<T>>> streamCodecMultimap(ResourceKey<? extends Registry<T>> registryKey) {

		StreamCodec<ByteBuf, TagKey<T>> codec = TagKey.streamCodec(registryKey);
		StreamCodec<ByteBuf, Map<TagKey<T>, Collection<TagKey<T>>>> codecMap = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, codec, codec.apply(ByteBufCodecs.collection(ObjectOpenHashSet::new)));

		return codecMap.map(
			map -> {

				ImmutableSetMultimap.Builder<TagKey<T>, TagKey<T>> builder = ImmutableSetMultimap.builder();
				map.forEach(builder::putAll);

				return builder.build();

			},
			ImmutableMultimap::asMap
		);

	}

}
