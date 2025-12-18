package io.github.eggohito.neo_apoli.util.comparison;

import com.google.common.base.Strings;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.text.DecimalFormat;
import java.util.function.Supplier;

public record NumberComparison(Comparator comparator, NumberProvider first, NumberProvider second, NumberProvider decimals) implements Comparison {

	public static final MapCodec<NumberComparison> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Comparator.CODEC.fieldOf("comparator").forGetter(NumberComparison::comparator),
		NumberProvider.CODEC.fieldOf("first").forGetter(NumberComparison::first),
		NumberProvider.CODEC.fieldOf("second").forGetter(NumberComparison::second),
		NumberProvider.CODEC.optionalFieldOf("decimals", new ConstantNumberProvider(0)).forGetter(NumberComparison::decimals)
	).apply(instance, NumberComparison::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, NumberComparison> STREAM_CODEC = StreamCodec.composite(
		Comparator.STREAM_CODEC, NumberComparison::comparator,
		NumberProvider.STREAM_CODEC, NumberComparison::first,
		NumberProvider.STREAM_CODEC, NumberComparison::second,
		NumberProvider.STREAM_CODEC, NumberComparison::decimals,
		NumberComparison::new
	);

	@Override
	public ComparisonType<?> type() {
		return ComparisonTypes.NUMBER;
	}

	@Override
	public boolean compare(Context context) {

		Context decimalsContext = context.forChild(".decimals");
		int decimals = decimals().nextInt(decimalsContext);

		if (decimalsContext.hasErrors()) {
			return false;
		}

		Context firstContext = context.forChild(".first");
		Context secondContext = context.forChild(".second");

		double firstValue = this.getValue(first(), decimals, () -> firstContext);
		double secondValue = this.getValue(second(), decimals, () -> secondContext);

		return !firstContext.hasErrors()
			&& !secondContext.hasErrors()
			&& comparator().compare(firstValue, secondValue);

	}

	@Override
	public void validate(Context.Validator validator) {

		Comparison.super.validate(validator);

		first().validate(validator.forChild(".first"));
		second().validate(validator.forChild(".second"));
		decimals().validate(validator.forChild(".decimals"));

	}

	private double getValue(NumberProvider provider, int decimals, Supplier<Context> contextSupplier) {

		Context context = contextSupplier.get();
		DecimalFormat decimalFormat = new DecimalFormat("#." + Strings.repeat("#", decimals));

		if (decimals == 0) {
			return provider.nextLong(context);
		}

		else {
			return Double.parseDouble(decimalFormat.format(provider.nextDouble(context)));
		}

	}

}
