package io.github.eggohito.neo_apoli.comparison;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.comparison.type.ComparisonTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NumberComparison(Comparator comparator, NumberProvider first, NumberProvider second) implements Comparison {

	public static final MapCodec<NumberComparison> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Comparator.CODEC.fieldOf("comparator").forGetter(NumberComparison::comparator),
		NumberProvider.CODEC.fieldOf("first").forGetter(NumberComparison::first),
		NumberProvider.CODEC.fieldOf("second").forGetter(NumberComparison::second)
	).apply(instance, NumberComparison::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, NumberComparison> STREAM_CODEC = StreamCodec.composite(
		Comparator.STREAM_CODEC, NumberComparison::comparator,
		NumberProvider.STREAM_CODEC, NumberComparison::first,
		NumberProvider.STREAM_CODEC, NumberComparison::second,
		NumberComparison::new
	);

	@Override
	public ComparisonType<?> type() {
		return ComparisonTypes.NUMBER;
	}

	@Override
	public boolean compare(Context context) {

		Context firstContext = context.forChild(".first");
		double firstValue = first().nextDouble(firstContext);

		if (firstContext.hasErrors()) {
			return false;
		}

		Context secondContext = context.forChild(".second");
		double secondValue = second().nextDouble(secondContext);

		if (secondContext.hasErrors()) {
			return false;
		}

		return comparator().compare(firstValue, secondValue);

	}

	@Override
	public void validate(Context.Validator validator) {

		Comparison.super.validate(validator);

		first().validate(validator.forChild(".first"));
		second().validate(validator.forChild(".second"));

	}

}
