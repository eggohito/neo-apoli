package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.*;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class MapCodecUtil {

	public static <A> MapCodec<A> lazy(String name, Supplier<MapCodec<A>> delegate) {
		return MapCodec.recursive(name, self -> delegate.get());
	}

	public static <A> MapCodec<A> lazy(Supplier<MapCodec<A>> delegate) {
		return lazy(delegate.toString(), delegate);
	}

	public static <A> MapCodec<A> fail(Supplier<String> error) {
		return new MapCodec<>() {

			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Stream.empty();
			}

			@Override
			public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
				return DataResult.error(error);
			}

			@Override
			public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				return prefix.withErrorsFrom(DataResult.error(error));
			}

		};
	}

}
