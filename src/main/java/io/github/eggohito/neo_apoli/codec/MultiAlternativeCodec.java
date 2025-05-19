package io.github.eggohito.neo_apoli.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Function;

public record MultiAlternativeCodec<T>(Codec<T> primary, Codec<? extends T>... alternatives) implements Codec<T> {

	@SafeVarargs
	public MultiAlternativeCodec {

	}

	@Override
	public <I> DataResult<Pair<T, I>> decode(DynamicOps<I> ops, I input) {

		StringBuilder errorBuilder = new StringBuilder();
		DataResult<Pair<T, I>> primaryResult = primary().decode(ops, input).ifError(error -> errorBuilder.append("\n\t - ").append(error));

		if (primaryResult.isSuccess()) {
			return primaryResult;
		}

		for (var alternative : alternatives()) {

			DataResult<Pair<T, I>> altResult = alternative.decode(ops, input)
				.ifError(error -> errorBuilder.append("\n\t - ").append(error))
				.map(pair -> pair.mapFirst(Function.identity()));

			if (altResult.isSuccess()) {
				return altResult;
			}

		}

		return DataResult.error(() -> "Failed to parse." + errorBuilder);

	}

	@Override
	public <I> DataResult<I> encode(T input, DynamicOps<I> ops, I prefix) {
		return primary().encode(input, ops, prefix);
	}

}
