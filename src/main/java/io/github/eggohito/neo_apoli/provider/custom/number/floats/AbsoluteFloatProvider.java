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
import org.jetbrains.annotations.NotNull;

public record AbsoluteFloatProvider(FloatProvider value) implements FloatProvider {

	public static final MapCodec<AbsoluteFloatProvider> CODEC = MapCodecUtil.lazy(AbsoluteFloatProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance
		.group(FloatProvider.CODEC.fieldOf("value").forGetter(AbsoluteFloatProvider::value))
		.apply(instance, AbsoluteFloatProvider::new))
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, AbsoluteFloatProvider> STREAM_CODEC = StreamCodecUtil.lazy(AbsoluteFloatProvider.class.getSimpleName(), () -> StreamCodec.composite(
		FloatProvider.STREAM_CODEC, AbsoluteFloatProvider::value,
		AbsoluteFloatProvider::new
	));

	@Override
	public @NotNull FloatProvider.Type<?> getType() {
		return NeoApoliFloatProviderTypes.ABSOLUTE;
	}

	@Override
	public void provideFloat(Context context, FloatConsumer setter) {
		value().provideFloat(context.forChild(".value"), value -> setter.accept(Math.abs(value)));
	}

	@Override
	public void validate(Context.Validator validator) {
		FloatProvider.super.validate(validator);
		value().validate(validator.forChild(".value"));
	}

}
