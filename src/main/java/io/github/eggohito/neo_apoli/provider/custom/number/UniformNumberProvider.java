package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.random.RandomGenerator;

public record UniformNumberProvider(Random random, NumberProvider min, NumberProvider max) implements NumberProvider{

	public static final MapCodec<UniformNumberProvider> MAP_CODEC = MapCodecUtil.lazy(UniformNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("min", new ConstantNumberProvider(0)).forGetter(UniformNumberProvider::min),
		NumberProvider.CODEC.fieldOf("max").forGetter(UniformNumberProvider::max)
	).apply(instance, UniformNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, UniformNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(UniformNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, UniformNumberProvider::min,
		NumberProvider.STREAM_CODEC, UniformNumberProvider::max,
		UniformNumberProvider::new
	));

	public UniformNumberProvider(NumberProvider min, NumberProvider max) {
		this(new Random(), min, max);
	}

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.UNIFORM;
	}

	@Override
	public double nextDouble(Context context) {
		return this.randomize(context, NumberProvider::nextDouble, RandomGenerator::nextDouble);
	}

	@Override
	public long nextLong(Context context) {
		return this.randomize(context, NumberProvider::nextLong, RandomGenerator::nextLong);
	}

	private <N extends Number & Comparable<N>> N randomize(Context context, BiFunction<NumberProvider, Context, N> getter, TriFunction<Random, N, N, N> method) {

		N min = getter.apply(min(), context.forChild(".min"));
		N max = getter.apply(max(), context.forChild(".max"));

		if (min.compareTo(max) >= 0) {
			return min;
		}

		else {
			return method.apply(random(), min, max);
		}

	}

}
