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

public record RandomUniformFloatProvider(FloatProvider min, FloatProvider max) implements FloatProvider {

	public static final MapCodec<RandomUniformFloatProvider> CODEC = MapCodecUtil.lazy(RandomUniformFloatProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		FloatProvider.CODEC.fieldOf("min").forGetter(RandomUniformFloatProvider::min),
		FloatProvider.CODEC.fieldOf("max").forGetter(RandomUniformFloatProvider::max)
	).apply(instance, RandomUniformFloatProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, RandomUniformFloatProvider> STREAM_CODEC = StreamCodecUtil.lazy(RandomUniformFloatProvider.class.getSimpleName(), () -> StreamCodec.composite(
		FloatProvider.STREAM_CODEC, RandomUniformFloatProvider::min,
		FloatProvider.STREAM_CODEC, RandomUniformFloatProvider::max,
		RandomUniformFloatProvider::new
	));

	@Override
	public @NotNull FloatProvider.Type<?> getType() {
		return NeoApoliFloatProviderTypes.RANDOM_UNIFORM;
	}

	@Override
	public void provideFloat(Context context, FloatConsumer setter) {

		Context minContext = context.forChild(".min");
		float min = min().getFloat(minContext);

		if (minContext.hasProblems()) {
			return;
		}

		Context maxContext = context.forChild(".max");
		float max = max().getFloat(maxContext);

		if (maxContext.hasProblems()) {
			setter.accept(min);
		}

		else {
			setter.accept(Mth.nextFloat(context.level().getRandom(), min, max));
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		FloatProvider.super.validate(validator);
		min().validate(validator.forChild(".min"));
		max().validate(validator.forChild(".max"));
	}

}
