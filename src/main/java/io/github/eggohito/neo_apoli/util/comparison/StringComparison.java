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

		String firstValue = first().stringValue(context.makeChild("first"));
		String secondValue = second().stringValue(context.makeChild("second"));

		if (!caseSensitive()) {
			firstValue = firstValue.toLowerCase(Locale.ROOT);
			secondValue = secondValue.toLowerCase(Locale.ROOT);
		}

		return !context.hasAnyErrors()
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
		return "String comparison (with first value: " + first().asDisplayString() + " and second value: " + second().asDisplayString() + ")";
	}

}
