package io.github.eggohito.neo_apoli.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public final class MultiAlternativeCodec<T> implements Codec<T> {

	private final Encoder<T> encoder;
	private final Decoder<T> decoder;

	public MultiAlternativeCodec(Codec<T> primary, List<Codec<? extends T>> alternatives) {

		Codec<T> decoder = primary;
		for (var alternative : alternatives) {
			decoder = Codec.withAlternative(decoder, alternative);
		}

		this.encoder = primary;
		this.decoder = decoder;

	}

	@SafeVarargs
	public MultiAlternativeCodec(Codec<T> primary, Codec<? extends T>... alternatives) {
		this(primary, new ObjectArrayList<>(alternatives));
	}

	@Override
	public <I> DataResult<Pair<T, I>> decode(DynamicOps<I> ops, I input) {
		return decoder.decode(ops, input);
	}

	@Override
	public <I> DataResult<I> encode(T input, DynamicOps<I> ops, I prefix) {
		return encoder.encode(input, ops, prefix);
	}

}
