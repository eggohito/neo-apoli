package io.github.eggohito.neo_apoli.util.comparison;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonType;
import io.github.eggohito.neo_apoli.util.comparison.type.ComparisonTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Objects;

public record NbtComparison(NbtProvider first, NbtProvider second) implements Comparison {

	public static final MapCodec<NbtComparison> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NbtProvider.CODEC.fieldOf("first").forGetter(NbtComparison::first),
		NbtProvider.CODEC.fieldOf("second").forGetter(NbtComparison::second)
	).apply(instance, NbtComparison::new));

	public static final PacketCodec<RegistryByteBuf, NbtComparison> PACKET_CODEC = PacketCodec.tuple(
		NbtProvider.PACKET_CODEC, NbtComparison::first,
		NbtProvider.PACKET_CODEC, NbtComparison::second,
		NbtComparison::new
	);

	@Override
	public ComparisonType<?> type() {
		return ComparisonTypes.NBT;
	}

	@Override
	public boolean compare(Context context) {

		Context firstContext = context.makeChild(".first");
		NbtElement first = first().next(firstContext);

		Context secondContext = context.makeChild(".second");
		NbtElement second = second().next(secondContext);

		return !firstContext.hasErrors()
			&& !secondContext.hasErrors()
			&& Objects.equals(first, second);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		Comparison.super.validate(reporter);

		first().validate(reporter.makeChild(".first"));
		second().validate(reporter.makeChild(".second"));

	}

}
