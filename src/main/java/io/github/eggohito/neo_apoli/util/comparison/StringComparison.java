package io.github.eggohito.neo_apoli.util.comparison;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Locale;

public record StringComparison(Comparator comparator, StringProvider first, StringProvider second, boolean caseSensitive) implements Comparison {

	public static final MapCodec<StringComparison> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Comparator.CODEC.fieldOf("comparator").forGetter(StringComparison::comparator),
		StringProvider.CODEC.fieldOf("first").forGetter(StringComparison::first),
		StringProvider.CODEC.fieldOf("second").forGetter(StringComparison::second),
		Codec.BOOL.optionalFieldOf("case_sensitive", true).forGetter(StringComparison::caseSensitive)
	).apply(instance, StringComparison::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, StringComparison> STREAM_CODEC = StreamCodec.composite(
		Comparator.STREAM_CODEC, StringComparison::comparator,
		StringProvider.STREAM_CODEC, StringComparison::first,
		StringProvider.STREAM_CODEC, StringComparison::second,
		ByteBufCodecs.BOOL, StringComparison::caseSensitive,
		StringComparison::new
	);

	@Override
	public ComparisonType<?> type() {
		return ComparisonTypes.STRING;
	}

	@Override
	public boolean compare(Context context) {

		Context firstContext = context.forChild(".first");
		Context secondContext = context.forChild(".second");

		String firstValue = first().next(firstContext);
		String secondValue = second().next(secondContext);

		if (!firstContext.hasErrors() && !secondContext.hasErrors()) {

			if (!this.caseSensitive()) {
				firstValue = firstValue.toLowerCase(Locale.ROOT);
				secondValue = secondValue.toLowerCase(Locale.ROOT);
			}

			return comparator().compare(firstValue, secondValue);

		}

		else {
			return false;
		}

	}

	@Override
	public void validate(ProblemReporter reporter) {

		Comparison.super.validate(reporter);

		first().validate(reporter.forChild(".first"));
		second().validate(reporter.forChild(".second"));

	}

}
