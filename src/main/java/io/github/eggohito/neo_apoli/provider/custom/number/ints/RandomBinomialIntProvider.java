package io.github.eggohito.neo_apoli.provider.custom.number.ints;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.IntProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliIntProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

public record RandomBinomialIntProvider(IntProvider attempts, FloatProvider probability) implements IntProvider {

	public static final MapCodec<RandomBinomialIntProvider> CODEC = MapCodecUtil.lazy(RandomBinomialIntProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		IntProvider.CODEC.fieldOf("attempts").forGetter(RandomBinomialIntProvider::attempts),
		FloatProvider.CODEC.fieldOf("probability").forGetter(RandomBinomialIntProvider::probability)
	).apply(instance, RandomBinomialIntProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, RandomBinomialIntProvider> STREAM_CODEC = StreamCodecUtil.lazy(RandomBinomialIntProvider.class.getSimpleName(), () -> StreamCodec.composite(
		IntProvider.STREAM_CODEC, RandomBinomialIntProvider::attempts,
		FloatProvider.STREAM_CODEC, RandomBinomialIntProvider::probability,
		RandomBinomialIntProvider::new
	));

	@Override
	public @NotNull IntProvider.Type<?> getType() {
		return NeoApoliIntProviderTypes.RANDOM_BINOMIAL;
	}

	@Override
	public void provideInt(Context context, IntConsumer setter) {

		Context attemptsContext = context.forChild(".attempts");
		int attempts = attempts().getInt(attemptsContext);

		if (attemptsContext.hasProblems()) {
			return;
		}

		Context probabilityContext = context.forChild(".probability");
		float probability = probability().getFloat(probabilityContext);

		if (probabilityContext.hasProblems()) {
			return;
		}

		RandomSource random = context.level().getRandom();
		int result = 0;

		for (int i = 0; i < attempts; ++i) {

			if (random.nextFloat() < probability) {
				result++;
			}

		}

		setter.accept(result);

	}

	@Override
	public void validate(Context.Validator validator) {
		IntProvider.super.validate(validator);
		attempts().validate(validator.forChild(".attempts"));
		probability().validate(validator.forChild(".probability"));
	}

}
