package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.random.RandomGenerator;

public record UniformNumberProvider(Random random, NumberProvider min, NumberProvider max) implements NumberProvider{

	public static final MapCodec<UniformNumberProvider> CODEC = MapCodecUtil.lazy(UniformNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("min", new ConstantNumberProvider(0)).forGetter(UniformNumberProvider::min),
		NumberProvider.CODEC.fieldOf("max").forGetter(UniformNumberProvider::max)
	).apply(instance, UniformNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, UniformNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(UniformNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, UniformNumberProvider::min,
		NumberProvider.PACKET_CODEC, UniformNumberProvider::max,
		UniformNumberProvider::new
	));

	public UniformNumberProvider(NumberProvider min, NumberProvider max) {
		this(new Random(), min, max);
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.UNIFORM;
	}

	@Override
	public @NotNull Number next(Context context) {
		return this.randomize(context, NumberProvider::nextDouble, RandomGenerator::nextDouble);
	}

	@Override
	public long nextLong(Context context) {
		return this.randomize(context, NumberProvider::nextLong, RandomGenerator::nextLong);
	}

	private <N extends Number> N randomize(Context context, BiFunction<NumberProvider, Context, N> getter, TriFunction<Random, N, N, N> method) {
		return method.apply(random(), getter.apply(min(), context.makeChild(".min")), getter.apply(max(), context.makeChild(".max")));
	}

}
