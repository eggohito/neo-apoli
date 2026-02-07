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
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public record BinomialNumberProvider(NumberProvider attempts, NumberProvider probability) implements NumberProvider {

	public static final MapCodec<BinomialNumberProvider> MAP_CODEC = MapCodecUtil.lazy(BinomialNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("attempts").forGetter(BinomialNumberProvider::attempts),
		NumberProvider.CODEC.fieldOf("probability").forGetter(BinomialNumberProvider::probability)
	).apply(instance, BinomialNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, BinomialNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(BinomialNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, BinomialNumberProvider::attempts,
		NumberProvider.STREAM_CODEC, BinomialNumberProvider::probability,
		BinomialNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BINOMIAL;
	}

	@Override
	public @NotNull Number next(Context context) {

		RandomSource random = context.level().getRandom();
		long result = 0;

		Context attemptsContext = context.forChild(".attempts");
		long attempts = attempts().nextLong(attemptsContext);

		Context probabilityContext = context.forChild(".probability");
		double probability = probability().nextDouble(probabilityContext);

		for (int i = 0; !attemptsContext.hasErrors() && !probabilityContext.hasErrors() && i < attempts; i++) {

			if (random.nextDouble() < probability) {
				result++;
			}

		}

		return result;

	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		attempts().validate(validator.forChild(".attempts"));
		probability().validate(validator.forChild(".probability"));

	}

}
