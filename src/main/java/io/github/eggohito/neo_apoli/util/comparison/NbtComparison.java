package io.github.eggohito.neo_apoli.util.comparison;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NbtComparison(NbtProvider first, NbtProvider second) implements Comparison {

	public static final MapCodec<NbtComparison> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NbtProvider.CODEC.fieldOf("first").forGetter(NbtComparison::first),
		NbtProvider.CODEC.fieldOf("second").forGetter(NbtComparison::second)
	).apply(instance, NbtComparison::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, NbtComparison> STREAM_CODEC = StreamCodec.composite(
		NbtProvider.STREAM_CODEC, NbtComparison::first,
		NbtProvider.STREAM_CODEC, NbtComparison::second,
		NbtComparison::new
	);

	@Override
	public ComparisonType<?> type() {
		return ComparisonTypes.NBT;
	}

	@Override
	public boolean compare(Context context) {

		Context firstContext = context.forChild(".first");
		Tag first = first().next(firstContext);

		Context secondContext = context.forChild(".second");
		Tag second = second().next(secondContext);

		return !firstContext.hasErrors()
			&& !secondContext.hasErrors()
			&& NbtUtils.compareNbt(first, second, true);

	}

	@Override
	public void validate(ProblemReporter reporter) {

		Comparison.super.validate(reporter);

		first().validate(reporter.forChild(".first"));
		second().validate(reporter.forChild(".second"));

	}

}
