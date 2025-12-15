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
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public record ClampedNumberProvider(NumberProvider value, NumberProvider min, NumberProvider max) implements NumberProvider {

	public static final MapCodec<ClampedNumberProvider> CODEC = MapCodecUtil.lazy(ClampedNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("value").forGetter(ClampedNumberProvider::value),
		NumberProvider.CODEC.fieldOf("min").forGetter(ClampedNumberProvider::min),
		NumberProvider.CODEC.fieldOf("max").forGetter(ClampedNumberProvider::max)
	).apply(instance, ClampedNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, ClampedNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(ClampedNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, ClampedNumberProvider::value,
		NumberProvider.STREAM_CODEC, ClampedNumberProvider::min,
		NumberProvider.STREAM_CODEC, ClampedNumberProvider::max,
		ClampedNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.CLAMPED;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context minContext = context.forChild(".min");
		double min = min().nextDouble(minContext);

		Context maxContext = context.forChild(".max");
		double max = max().nextDouble(maxContext);

		Context valueContext = context.forChild(".value");
		double value = value().nextDouble(valueContext);

		if (minContext.hasErrors() || maxContext.hasErrors()) {
			return value;
		}

		else {
			return Mth.clamp(value, min, max);
		}

	}

	@Override
	public void validate(ProblemReporter reporter) {

		NumberProvider.super.validate(reporter);

		value().validate(reporter.forChild(".value"));
		min().validate(reporter.forChild(".min"));
		max().validate(reporter.forChild(".max"));

	}

}
