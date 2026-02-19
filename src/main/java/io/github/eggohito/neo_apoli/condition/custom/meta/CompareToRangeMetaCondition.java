package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.stream.Stream;

public interface CompareToRangeMetaCondition extends Condition {

	NumberProvider value();

	Optional<NumberProvider> min();

	Optional<NumberProvider> max();

	@Override
	default boolean test(Context context) {

		double value = value().nextDouble(context.forChild(".value"));
		Optional<Double> min = min().map(provider -> provider.nextDouble(context.forChild(".min")));
		Optional<Double> max = max().map(provider -> provider.nextDouble(context.forChild(".max")));

		boolean minBiggerThanMax = min.isPresent() && max.isPresent()
								&& min.get() > max.get();

		if (minBiggerThanMax) {
			context.reportProblem("Minimum value cannot be bigger than the maximum value! (min: " + min + ", max: " + max + ")");
		}

		return !minBiggerThanMax
			&& (min.isEmpty() || !(min.get() > value))
			&& (max.isEmpty() || !(max.get() < value));

	}

	@Override
	default void validate(Context.Validator validator) {

		Condition.super.validate(validator);
		value().validate(validator.forChild(".value"));

		min().ifPresent(min -> min.validate(validator.forChild(".min")));
		max().ifPresent(max -> max.validate(validator.forChild(".max")));

	}

	static <M extends CompareToRangeMetaCondition> MapCodec<M> mapCodec(Function3<NumberProvider, Optional<NumberProvider>, Optional<NumberProvider>, M> constructor) {
		return new MapCodec<>() {

			private static final MapCodec<NumberProvider> VALUE_CODEC = NumberProvider.CODEC.fieldOf("value");
			private static final MapCodec<Optional<NumberProvider>> MIN_CODEC = NumberProvider.CODEC.optionalFieldOf("min");
			private static final MapCodec<Optional<NumberProvider>> MAX_CODEC = NumberProvider.CODEC.optionalFieldOf("max");

			@Override
			public <I> Stream<I> keys(DynamicOps<I> ops) {
				return Streams.concat(VALUE_CODEC.keys(ops), MIN_CODEC.keys(ops), MAX_CODEC.keys(ops));
			}

			@Override
			public <I> DataResult<M> decode(DynamicOps<I> ops, MapLike<I> input) {
				return MAX_CODEC.decode(ops, input)
					.flatMap(max -> MIN_CODEC.decode(ops, input)
						.flatMap(min -> VALUE_CODEC.decode(ops, input)
							.flatMap(value -> this.validate(value, min, max, input))));
			}

			@Override
			public <I> RecordBuilder<I> encode(M input, DynamicOps<I> ops, RecordBuilder<I> prefix) {
				return MAX_CODEC
					.encode(input.max(), ops, MIN_CODEC
						.encode(input.min(), ops, VALUE_CODEC
							.encode(input.value(), ops, prefix)));
			}

			private <I> DataResult<M> validate(NumberProvider valueProvider, Optional<NumberProvider> minProvider, Optional<NumberProvider> maxProvider, MapLike<I> input) {

				if (minProvider.isEmpty() && maxProvider.isEmpty()) {
					return DataResult.error(() -> "Any of 'min' or 'max' keys must be present in input: " + input);
				}

				else {
					return DataResult.success(constructor.apply(valueProvider, minProvider, maxProvider));
				}

			}

		};
	}

	static <M extends CompareToRangeMetaCondition> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function3<NumberProvider, Optional<NumberProvider>, Optional<NumberProvider>, M> constructor) {
		return StreamCodec.composite(
			NumberProvider.STREAM_CODEC, CompareToRangeMetaCondition::value,
			ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), CompareToRangeMetaCondition::min,
			ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), CompareToRangeMetaCondition::max,
			constructor
		);
	}

}
