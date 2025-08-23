package io.github.eggohito.neo_apoli.condition.meta;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.stream.Stream;

public interface CompareToRangeMetaCondition {

	NumberProvider value();

	Optional<NumberProvider> min();

	Optional<NumberProvider> max();

	@ApiStatus.Internal
	default boolean internalImpl(Context context) {

		Context valueContext = context.makeChild(".value");
		double value = value().nextDouble(valueContext);

		if (valueContext.hasErrors()) {
			return false;
		}

		Context minContext = context.makeChild(".min");
		Optional<Double> min = min().map(minProvider -> minProvider.nextDouble(minContext));

		Context maxContext = context.makeChild(".max");
		Optional<Double> max = max().map(maxProvider -> maxProvider.nextDouble(maxContext));

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

	default void validate(ContextAware.ErrorReporter reporter) {

		value().validate(reporter.makeChild(".value"));

		min().ifPresent(minProvider -> minProvider.validate(reporter.makeChild(".min")));
		max().ifPresent(maxProvider -> maxProvider.validate(reporter.makeChild(".max")));

	}

	static <M extends CompareToRangeMetaCondition> MapCodec<M> codec(Function3<NumberProvider, Optional<NumberProvider>, Optional<NumberProvider>, M> constructor) {
		return new MapCodec<>() {

			private static final MapCodec<NumberProvider> VALUE_FIELD = NumberProvider.CODEC.fieldOf("value");
			private static final MapCodec<Optional<NumberProvider>> MIN_FIELD = NumberProvider.CODEC.optionalFieldOf("min");
			private static final MapCodec<Optional<NumberProvider>> MAX_FIELD = NumberProvider.CODEC.optionalFieldOf("max");

			@Override
			public <I> Stream<I> keys(DynamicOps<I> ops) {
				return Stream.of("value", "min", "max").map(ops::createString);
			}

			@Override
			public <I> DataResult<M> decode(DynamicOps<I> ops, MapLike<I> input) {
				return MAX_FIELD.decode(ops, input)
					.flatMap(max -> MIN_FIELD.decode(ops, input)
						.flatMap(min -> VALUE_FIELD.decode(ops, input)
							.flatMap(value -> this.validate(value, min, max, input))));
			}

			@Override
			public <I> RecordBuilder<I> encode(M input, DynamicOps<I> ops, RecordBuilder<I> prefix) {
				return MAX_FIELD
					.encode(input.max(), ops, MIN_FIELD
						.encode(input.min(), ops, VALUE_FIELD
							.encode(input.value(), ops, prefix)));
			}

			private <I> DataResult<M> validate(NumberProvider valueProvider, Optional<NumberProvider> minProvider, Optional<NumberProvider> maxProvider, MapLike<I> input) {

				if (minProvider.isEmpty() && maxProvider.isEmpty()) {
					return DataResult.error(() -> "Any of 'min' and 'max' keys must be present in input: " + input);
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
