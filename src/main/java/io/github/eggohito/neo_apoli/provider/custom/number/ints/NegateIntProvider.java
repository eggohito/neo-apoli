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

public record NegateIntProvider(IntProvider value) implements IntProvider {

	public static final MapCodec<NegateIntProvider> CODEC = MapCodecUtil.lazy(NegateIntProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance
		.group(IntProvider.CODEC.fieldOf("value").forGetter(NegateIntProvider::value))
		.apply(instance, NegateIntProvider::new)
	));

	public static final StreamCodec<RegistryFriendlyByteBuf, NegateIntProvider> STREAM_CODEC = StreamCodecUtil.lazy(NegateIntProvider.class.getSimpleName(), () -> StreamCodec.composite(
		IntProvider.STREAM_CODEC, NegateIntProvider::value,
		NegateIntProvider::new
	));

	@Override
	public @NotNull IntProvider.Type<?> getType() {
		return NeoApoliIntProviderTypes.NEGATE;
	}

	@Override
	public void provideInt(Context context, IntConsumer setter) {
		var valueContext = context.forChild(".value");
		value().provideInt(valueContext, value -> {

			try {
				setter.accept(Math.negateExact(value));
			}

			catch (ArithmeticException e) {
				valueContext.reportProblem(e.getMessage());
			}

		});
	}

	@Override
	public void validate(Context.Validator validator) {
		IntProvider.super.validate(validator);
		value().validate(validator.forChild(".value"));
	}

}
