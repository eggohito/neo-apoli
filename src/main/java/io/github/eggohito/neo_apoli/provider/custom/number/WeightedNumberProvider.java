package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.WeightedList;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public record WeightedNumberProvider(WeightedList<NumberProvider> entries) implements NumberProvider {

	public static final MapCodec<WeightedNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(WeightedList.codec(NumberProvider.CODEC).fieldOf("entries").forGetter(WeightedNumberProvider::entries))
		.apply(instance, WeightedNumberProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedNumberProvider> STREAM_CODEC = StreamCodec.composite(
		StreamCodecUtil.weightedList(NumberProvider.STREAM_CODEC), WeightedNumberProvider::entries,
		WeightedNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.WEIGHTED;
	}

	@Override
	public double getDouble(Context context) {
		return this.getRandom(context, NumberProvider::getDouble, 0.0D);
	}

	@Override
	public long getLong(Context context) {
		return this.getRandom(context, NumberProvider::getLong, 0L);
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		MiscUtil.iterateList(entries().unwrap(), (index, entry) -> entry.value().validate(validator.forChild(".entries[" + index + "]")));
	}

	private <N extends Number & Comparable<N>> N getRandom(Context context, BiFunction<NumberProvider, Context, N> getter, N defaultValue) {
		return entries().neo_apoli$getRandomAndIndex(context.level().getRandom())
			.map(pair -> getter.apply(pair.first(), context.forChild(".entries[" + pair.secondInt() + "]")))
			.orElse(defaultValue);
	}

}
