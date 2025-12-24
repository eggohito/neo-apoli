package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ExponentialNumberProvider(NumberProvider base, NumberProvider exponent) implements NumberProvider {

	public static final MapCodec<ExponentialNumberProvider> CODEC = MapCodecUtil.lazy(ExponentialNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("base").forGetter(ExponentialNumberProvider::base),
		NumberProvider.CODEC.fieldOf("exponent").forGetter(ExponentialNumberProvider::exponent)
	).apply(instance, ExponentialNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, ExponentialNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(ExponentialNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, ExponentialNumberProvider::base,
		NumberProvider.STREAM_CODEC, ExponentialNumberProvider::exponent,
		ExponentialNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.EXPONENTIAL;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context baseContext = context.forChild(".base");
		double base = base().nextDouble(baseContext);

		if (baseContext.hasErrors()) {
			return 0.0d;
		}

		Context exponentContext = context.forChild(".exponent");
		double exponent = exponent().nextDouble(exponentContext);

		if (exponentContext.hasErrors()) {
			return 0.0d;
		}

		return Math.pow(base, exponent);

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		base().validate(validator.forChild(".base"));
		exponent().validate(validator.forChild(".exponent"));
	}

}
