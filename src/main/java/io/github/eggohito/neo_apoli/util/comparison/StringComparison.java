package io.github.eggohito.neo_apoli.util.comparison;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Locale;

public record StringComparison(Comparator comparator, StringProvider first, StringProvider second, boolean caseSensitive) implements Comparison {

	public static final MapCodec<StringComparison> CODEC = RecordCodecBuilder.mapCodec(instance -> Comparison.addComparatorField(instance)
		.and(StringProvider.CODEC.fieldOf("first").forGetter(StringComparison::first))
		.and(StringProvider.CODEC.fieldOf("second").forGetter(StringComparison::second))
		.and(Codec.BOOL.optionalFieldOf("case_sensitive", true).forGetter(StringComparison::caseSensitive))
		.apply(instance, StringComparison::new));

	public static final PacketCodec<RegistryByteBuf, StringComparison> PACKET_CODEC = Comparison.createPacketCodec(
		(buf, comparison) -> {
			StringProvider.PACKET_CODEC.encode(buf, comparison.first());
			StringProvider.PACKET_CODEC.encode(buf, comparison.second());
			buf.writeBoolean(comparison.caseSensitive());
		},
		(buf, comparator) -> new StringComparison(comparator,
			StringProvider.PACKET_CODEC.decode(buf),
			StringProvider.PACKET_CODEC.decode(buf),
			buf.readBoolean()
		)
	);

	@Override
	public ComparisonType<?> type() {
		return ComparisonTypes.STRING;
	}

	@Override
	public boolean compare(Context context) {

		Context firstContext = context.makeChild(".first");
		Context secondContext = context.makeChild(".second");

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
	public void validate(ErrorReporter reporter) {

		Comparison.super.validate(reporter);

		first().validate(reporter.makeChild(".first"));
		second().validate(reporter.makeChild(".second"));

	}

}
