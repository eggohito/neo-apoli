package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Optional;
import java.util.stream.Stream;

public interface CompareToRangeMetaCondition extends MetaCondition {

	NumberProvider value();

	Optional<NumberProvider> min();

	Optional<NumberProvider> max();

	@Override
	default boolean test(Context context) {

		Context valueContext = context.makeChild(".value");
		double value = value().nextDouble(valueContext);

		if (valueContext.hasErrors()) {
			return false;
		}

		Context minContext = context.makeChild(".min");
		Optional<Double> min = min().map(provider -> provider.nextDouble(minContext));

		Context maxContext = context.makeChild(".max");
		Optional<Double> max = max().map(provider -> provider.nextDouble(maxContext));

		if (minContext.hasErrors() || maxContext.hasErrors()) {
			return false;
		}

		else if (min.isPresent() && max.isPresent() && min.get() > max.get()) {
			context.getReporter().report("Minimum value cannot be bigger than maximum value");
			return false;
		}

		else {
			return (min.isEmpty() || !(min.get() > value))
				&& (max.isEmpty() || !(max.get() < value));
		}

	}

	@Override
	default void validate(ErrorReporter reporter) {

		value().validate(reporter.makeChild(".value"));

		min().ifPresent(min -> min.validate(reporter.makeChild(".min")));
		max().ifPresent(max -> max.validate(reporter.makeChild(".max")));

	}

	static <M extends CompareToRangeMetaCondition> MapCodec<M> codec(Function3<NumberProvider, Optional<NumberProvider>, Optional<NumberProvider>, M> constructor) {
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

	static <M extends CompareToRangeMetaCondition> PacketCodec<RegistryByteBuf, M> packetCodec(Function3<NumberProvider, Optional<NumberProvider>, Optional<NumberProvider>, M> constructor) {
		return PacketCodec.tuple(
			NumberProvider.PACKET_CODEC, CompareToRangeMetaCondition::value,
			PacketCodecs.optional(NumberProvider.PACKET_CODEC), CompareToRangeMetaCondition::min,
			PacketCodecs.optional(NumberProvider.PACKET_CODEC), CompareToRangeMetaCondition::max,
			constructor
		);
	}

}
