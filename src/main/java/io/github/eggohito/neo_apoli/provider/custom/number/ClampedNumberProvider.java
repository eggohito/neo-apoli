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
import net.minecraft.util.Mth;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public record ClampedNumberProvider(NumberProvider value, NumberProvider min, NumberProvider max) implements NumberProvider {

	public static final MapCodec<ClampedNumberProvider> MAP_CODEC = MapCodecUtil.lazy(ClampedNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
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
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.CLAMPED;
	}

	@Override
	public double nextDouble(Context context) {
		return this.nextOrElse(context, NumberProvider::nextDouble, Mth::clamp);
	}

	@Override
	public long nextLong(Context context) {
		return this.nextOrElse(context, NumberProvider::nextLong, Mth::clamp);
	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		value().validate(validator.forChild(".value"));
		min().validate(validator.forChild(".min"));
		max().validate(validator.forChild(".max"));

	}

	private <N extends Number> N nextOrElse(Context context, BiFunction<NumberProvider, Context, N> getter, TriFunction<N, N, N, N> clamp) {

		Context valueContext = context.forChild(".value");
		N value = getter.apply(value(), valueContext);

		Context minContext = context.forChild(".min");
		N min = getter.apply(min(), minContext);

		if (minContext.hasErrors()) {
			return value;
		}

		Context maxContext = context.forChild(".max");
		N max = getter.apply(max(), maxContext);

		if (maxContext.hasErrors()) {
			return value;
		}

		return clamp.apply(value, min, max);

	}

}
