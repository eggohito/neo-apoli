package io.github.eggohito.neo_apoli.codec;

import com.mojang.serialization.MapCodec;

import java.util.function.Supplier;

public class NeoApoliMapCodecs {

	public static <A> MapCodec<A> lazy(String name, Supplier<MapCodec<A>> delegate) {
		return MapCodec.recursive(name, self -> delegate.get());
	}

	public static <A> MapCodec<A> lazy(Supplier<MapCodec<A>> delegate) {
		return lazy(delegate.toString(), delegate);
	}

}
