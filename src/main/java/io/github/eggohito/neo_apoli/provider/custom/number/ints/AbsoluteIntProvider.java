package io.github.eggohito.neo_apoli.provider.custom.number.ints;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.IntProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliIntProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

public record AbsoluteIntProvider(IntProvider value) implements IntProvider {

	public static final MapCodec<AbsoluteIntProvider> CODEC = MapCodecUtil.lazy(AbsoluteIntProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance
		.group(IntProvider.CODEC.fieldOf("value").forGetter(AbsoluteIntProvider::value))
		.apply(instance, AbsoluteIntProvider::new))
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, AbsoluteIntProvider> STREAM_CODEC = StreamCodecUtil.lazy(AbsoluteIntProvider.class.getSimpleName(), () -> StreamCodec.composite(
		IntProvider.STREAM_CODEC, AbsoluteIntProvider::value,
		AbsoluteIntProvider::new
	));

	@Override
	public @NotNull IntProvider.Type<?> getType() {
		return NeoApoliIntProviderTypes.ABSOLUTE;
	}

	@Override
	public void provideInt(Context context, IntConsumer setter) {
		value().provideInt(context.forChild(".value"), value -> setter.accept(Math.abs(value)));
	}

	@Override
	public void validate(Context.Validator validator) {
		IntProvider.super.validate(validator);
		value().validate(validator.forChild(".value"));
	}

}
