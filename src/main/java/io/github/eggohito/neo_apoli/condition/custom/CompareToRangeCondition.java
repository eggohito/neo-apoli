package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements Condition {

	private static final MapCodec<CompareToRangeCondition> UNVALIDATED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("value").forGetter(CompareToRangeCondition::value),
		NumberProvider.CODEC.optionalFieldOf("min").forGetter(CompareToRangeCondition::min),
		NumberProvider.CODEC.optionalFieldOf("max").forGetter(CompareToRangeCondition::max)
	).apply(instance, CompareToRangeCondition::new));

	public static final MapCodec<CompareToRangeCondition> CODEC = UNVALIDATED_CODEC.mapResult(new MapCodec.ResultFunction<>() {

		@Override
		public <T> DataResult<CompareToRangeCondition> apply(DynamicOps<T> ops, MapLike<T> input, DataResult<CompareToRangeCondition> result) {
			return result.flatMap(condition -> {

				if (condition.min().isEmpty() && condition.max().isEmpty()) {
					return DataResult.error(() -> "Any of 'min' or 'max' keys must be present in input: " + input);
				}

				else {
					return DataResult.success(condition);
				}

			});
		}

		@Override
		public <T> RecordBuilder<T> coApply(DynamicOps<T> ops, CompareToRangeCondition input, RecordBuilder<T> prefix) {
			return prefix;
		}

	});

	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeCondition> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, CompareToRangeCondition::value,
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), CompareToRangeCondition::min,
		ByteBufCodecs.optional(NumberProvider.STREAM_CODEC), CompareToRangeCondition::max,
		CompareToRangeCondition::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public boolean test(Context context) {

		double value = value().getDouble(context.forChild(".value"));
		Optional<Double> min = min().map(_min -> _min.getDouble(context.forChild(".min")));
		Optional<Double> max = max().map(_max -> _max.getDouble(context.forChild(".max")));

		boolean minBiggerThanMax = min.isPresent()
			&& max.isPresent()
			&& min.get() > max.get();

		if (minBiggerThanMax) {
			context.reportProblem("Minimum value cannot be bigger than the maximum value! (min: " + min.get() + ", max: " + max.get() + ")");
		}

		return !minBiggerThanMax
			&& (min.isEmpty() || !(min.get() > value))
			&& (max.isEmpty() || !(max.get() < value));

	}

	@Override
	public void validate(Context.Validator validator) {
		Condition.super.validate(validator);
		value().validate(validator.forChild(".value"));
		min().ifPresent(min -> min.validate(validator.forChild(".min")));
		max().ifPresent(max -> max.validate(validator.forChild(".max")));
	}

}
