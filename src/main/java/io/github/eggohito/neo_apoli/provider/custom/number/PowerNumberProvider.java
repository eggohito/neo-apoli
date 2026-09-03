package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record PowerNumberProvider(NumberProvider base, NumberProvider exponent) implements NumberProvider {

	public static final MapCodec<PowerNumberProvider> CODEC = MapCodecUtil.lazy(PowerNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("base").forGetter(PowerNumberProvider::base),
		NumberProvider.CODEC.fieldOf("exponent").forGetter(PowerNumberProvider::exponent)
	).apply(instance, PowerNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, PowerNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(PowerNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, PowerNumberProvider::base,
		NumberProvider.STREAM_CODEC, PowerNumberProvider::exponent,
		PowerNumberProvider::new
	));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.POWER;
	}

	@Override
	public double getDouble(Context context) {

		Context baseContext = context.forChild(".base");
		double base = base().getDouble(baseContext);

		if (baseContext.hasProblems()) {
			return 0.0d;
		}

		Context exponentContext = context.forChild(".exponent");
		double exponent = exponent().getDouble(exponentContext);

		if (exponentContext.hasProblems()) {
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
