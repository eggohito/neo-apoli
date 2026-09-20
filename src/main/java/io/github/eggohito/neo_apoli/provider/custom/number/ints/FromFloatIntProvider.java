package io.github.eggohito.neo_apoli.provider.custom.number.ints;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.IntProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliIntProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

public record FromFloatIntProvider(FloatProvider value) implements IntProvider {

	public static final MapCodec<FromFloatIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(FloatProvider.CODEC.fieldOf("value").forGetter(FromFloatIntProvider::value))
		.apply(instance, FromFloatIntProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, FromFloatIntProvider> STREAM_CODEC = StreamCodec.composite(
		FloatProvider.STREAM_CODEC, FromFloatIntProvider::value,
		FromFloatIntProvider::new
	);

	@Override
	public @NotNull IntProvider.Type<?> getType() {
		return NeoApoliIntProviderTypes.FROM_FLOAT;
	}

	@Override
	public void provideInt(Context context, IntConsumer setter) {
		value().provideFloat(context.forChild(".value"), value -> setter.accept((int) value));
	}

	@Override
	public void validate(Context.Validator validator) {
		IntProvider.super.validate(validator);
		value().validate(validator.forChild(".value"));
	}

}
