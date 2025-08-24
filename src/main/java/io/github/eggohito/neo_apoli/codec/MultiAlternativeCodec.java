package io.github.eggohito.neo_apoli.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public record MultiAlternativeCodec<T>(Codec<T> primary, List<Codec<? extends T>> alternatives) implements Codec<T> {

	@SafeVarargs
	public MultiAlternativeCodec(Codec<T> primary, Codec<? extends T>... alternatives) {
		this(primary, ObjectArrayList.of(alternatives));
	}

	@Override
	public <I> DataResult<Pair<T, I>> decode(DynamicOps<I> ops, I input) {

		Set<String> errors = new ObjectOpenHashSet<>();
		StringBuilder errorBuilder = new StringBuilder();

		DataResult<Pair<T, I>> primaryResult = primary().decode(ops, input)
			.ifError(error -> errors.add(error.message()));

		if (primaryResult.isSuccess()) {
			return primaryResult;
		}

		for (var alternative: alternatives()) {

			DataResult<Pair<T, I>> alternativeResult = alternative.decode(ops, input)
				.ifError(error -> errors.add(error.message()))
				.map(pair -> pair.mapFirst(Function.identity()));

			if (alternativeResult.isSuccess()) {
				return alternativeResult;
			}

		}

		boolean moreThanOneErrors = errors.size() > 1;
		errors.forEach(error -> errorBuilder
			.append(moreThanOneErrors && !error.startsWith("\n\t - ") ? "\n\t - " : "")
			.append(error));

		return DataResult.error(errorBuilder::toString);

	}

	@Override
	public <I> DataResult<I> encode(T input, DynamicOps<I> ops, I prefix) {
		return primary().encode(input, ops, prefix);
	}

}
