package io.github.eggohito.neo_apoli.provider.custom.number.floats;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliFloatProviderTypes;
import io.github.eggohito.neo_apoli.util.FloatConsumer;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public record ClampedFloatProvider(FloatProvider value, FloatProvider min, FloatProvider max) implements FloatProvider {

	public static final MapCodec<ClampedFloatProvider> CODEC = MapCodecUtil.lazy(ClampedFloatProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		FloatProvider.CODEC.fieldOf("value").forGetter(ClampedFloatProvider::value),
		FloatProvider.CODEC.fieldOf("min").forGetter(ClampedFloatProvider::min),
		FloatProvider.CODEC.fieldOf("max").forGetter(ClampedFloatProvider::max)
	).apply(instance, ClampedFloatProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, ClampedFloatProvider> STREAM_CODEC = StreamCodecUtil.lazy(ClampedFloatProvider.class.getSimpleName(), () -> StreamCodec.composite(
		FloatProvider.STREAM_CODEC, ClampedFloatProvider::value,
		FloatProvider.STREAM_CODEC, ClampedFloatProvider::min,
		FloatProvider.STREAM_CODEC, ClampedFloatProvider::max,
		ClampedFloatProvider::new
	));

	@Override
	public @NotNull FloatProvider.Type<?> getType() {
		return NeoApoliFloatProviderTypes.CLAMPED;
	}

	@Override
	public void provideFloat(Context context, FloatConsumer setter) {

		Context valueContext = context.forChild(".value");
		float value = value().getFloat(valueContext);

		if (valueContext.hasProblems()) {
			return;
		}

		Context minContext = context.forChild(".min");
		float min = min().getFloat(minContext);

		if (minContext.hasProblems()) {
			setter.accept(value);
		}

		else {

			Context maxContext = context.forChild(".max");
			float max = max().getFloat(maxContext);

			if (maxContext.hasProblems()) {
				setter.accept(Math.max(value, min));
			}

			else {
				setter.accept(Mth.clamp(value, min, max));
			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		FloatProvider.super.validate(validator);
		value().validate(validator.forChild(".value"));
		min().validate(validator.forChild(".min"));
		max().validate(validator.forChild(".max"));
	}

}
