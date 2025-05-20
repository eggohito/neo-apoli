package io.github.eggohito.neo_apoli.util.comparison;

import com.google.common.base.Strings;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.meta.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.text.DecimalFormat;
import java.util.function.Supplier;

public record NumberComparison(Comparator comparator, NumberProvider first, NumberProvider second, NumberProvider decimals) implements Comparison {

	public static final MapCodec<NumberComparison> CODEC = RecordCodecBuilder.mapCodec(instance -> Comparison.addComparatorField(instance)
		.and(NumberProvider.CODEC.fieldOf("first").forGetter(NumberComparison::first))
		.and(NumberProvider.CODEC.fieldOf("second").forGetter(NumberComparison::second))
		.and(NumberProvider.CODEC.optionalFieldOf("decimals", new ConstantNumberProvider(0)).forGetter(NumberComparison::decimals))
		.apply(instance, NumberComparison::new));

	public static final PacketCodec<RegistryByteBuf, NumberComparison> PACKET_CODEC = Comparison.createPacketCodec(
		(buf, comparison) -> {
			NumberProvider.PACKET_CODEC.encode(buf, comparison.first());
			NumberProvider.PACKET_CODEC.encode(buf, comparison.second());
			NumberProvider.PACKET_CODEC.encode(buf, comparison.decimals());
		},
		(buf, comparator) -> new NumberComparison(comparator,
			NumberProvider.PACKET_CODEC.decode(buf),
			NumberProvider.PACKET_CODEC.decode(buf),
			NumberProvider.PACKET_CODEC.decode(buf)
		)
	);

	@Override
	public ComparisonType<?> type() {
		return ComparisonTypes.NUMBER;
	}

	@Override
	public boolean compare(Context context) {

		Context decimalsContext = context.makeChild("decimals");
		int decimals = decimals().intValue(decimalsContext);

		if (decimalsContext.hasErrors()) {
			return false;
		}

		Context firstContext = context.makeChild("first");
		Context secondContext = context.makeChild("second");

		double firstValue = this.getValue(first(), decimals, () -> firstContext);
		double secondValue = this.getValue(second(), decimals, () -> secondContext);

		return (!firstContext.hasErrors() && !secondContext.hasErrors())
			&& comparator().compare(firstValue, secondValue);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		Comparison.super.validate(reporter);

		first().validate(reporter.makeChild("first"));
		second().validate(reporter.makeChild("second"));

	}

	@Override
	public String asDisplayString() {
		return "Number comparison (with first value: " + first().asDisplayString() + " and second value: " + second().asDisplayString() + ")";
	}

	private double getValue(NumberProvider provider, int decimals, Supplier<Context> contextSupplier) {

		Context context = contextSupplier.get();
		if (decimals == 0) {
			return provider.longValue(context);
		}

		else {
			DecimalFormat decimalFormat = new DecimalFormat("#." + Strings.repeat("#", decimals));
			return Double.parseDouble(decimalFormat.format(provider.doubleValue(context)));
		}

	}

}
