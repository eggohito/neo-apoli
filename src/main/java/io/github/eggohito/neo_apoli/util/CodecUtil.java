package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.FilteredUnboundedMapCodec;

import java.util.Set;

public class CodecUtil {

	public static <K, V> FilteredUnboundedMapCodec<K, V> filteredUnboundedMap(final Codec<K> keyCodec, final Codec<V> elementCodec, K... excludedKeys) {
		return filteredUnboundedMap(keyCodec, elementCodec, Set.of(excludedKeys));
	}

	public static <K, V> FilteredUnboundedMapCodec<K, V> filteredUnboundedMap(final Codec<K> keyCodec, final Codec<V> elementCodec, Set<K> excludedKeys) {
		return new FilteredUnboundedMapCodec<>(keyCodec, elementCodec, excludedKeys);
	}

}
