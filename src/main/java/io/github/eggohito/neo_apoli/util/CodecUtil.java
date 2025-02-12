package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.FilteredUnboundedMapCodec;

import java.util.function.Predicate;

public class CodecUtil {

	public static <K, V> FilteredUnboundedMapCodec<K, V> filteredUnboundedMap(final Codec<K> keyCodec, final Codec<V> elementCodec, Predicate<K> keyFilter) {
		return new FilteredUnboundedMapCodec<>(keyCodec, elementCodec, keyFilter);
	}

}
