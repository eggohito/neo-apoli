package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.random.RandomSeed;

import java.util.Random;
import java.util.function.BiFunction;

public record UniformNumberProvider(NumberProvider min, NumberProvider max, Random random) implements NumberProvider {

	public static final MapCodec<UniformNumberProvider> CODEC = NeoApoliMapCodecs.lazy(UniformNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("min", new ConstantNumberProvider(0)).forGetter(UniformNumberProvider::min),
		NumberProvider.CODEC.fieldOf("max").forGetter(UniformNumberProvider::max)
	).apply(instance, UniformNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, UniformNumberProvider> PACKET_CODEC = NeoApoliPacketCodecs.lazy(UniformNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, UniformNumberProvider::min,
		NumberProvider.PACKET_CODEC, UniformNumberProvider::max,
		UniformNumberProvider::new
	));

	public UniformNumberProvider(NumberProvider min, NumberProvider max) {
		this(min, max, new Random(RandomSeed.getSeed()));
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.UNIFORM;
	}

	@Override
	public double doubleValue(Context context) {
		return random(context, NumberProvider::doubleValue, random()::nextDouble);
	}

	@Override
	public long longValue(Context context) {
		return random(context, NumberProvider::longValue, random()::nextLong);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);

		min().validate(reporter.makeChild("min"));
		max().validate(reporter.makeChild("max"));

	}

	private <N extends Number> N random(Context context, BiFunction<NumberProvider, Context, N> getter, BiFunction<N, N, N> method) {
		return method.apply(getter.apply(min(), context.makeChild("min")), getter.apply(max(), context.makeChild("max")));
	}

}
