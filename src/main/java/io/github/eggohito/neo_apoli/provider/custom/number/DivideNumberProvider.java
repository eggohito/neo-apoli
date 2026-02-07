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
import org.jetbrains.annotations.NotNull;

public record DivideNumberProvider(NumberProvider dividend, NumberProvider divisor) implements NumberProvider {

	public static final MapCodec<DivideNumberProvider> MAP_CODEC = MapCodecUtil.lazy(DivideNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("dividend").forGetter(DivideNumberProvider::dividend),
		NumberProvider.CODEC.fieldOf("divisor").forGetter(DivideNumberProvider::divisor)
	).apply(instance, DivideNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, DivideNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(DivideNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, DivideNumberProvider::dividend,
		NumberProvider.STREAM_CODEC, DivideNumberProvider::divisor,
		DivideNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DIVIDE;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context dividendContext = context.forChild(".dividend");
		double dividend = dividend().nextDouble(dividendContext);

		if (dividendContext.hasErrors()) {
			return 0.0d;
		}

		Context divisorContext = context.forChild(".divisor");
		double divisor = divisor().nextDouble(divisorContext);

		if (divisorContext.hasErrors()) {
			return 0.0d;
		}

		return dividend / divisor;

	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		dividend().validate(validator.forChild(".dividend"));
		divisor().validate(validator.forChild(".divisor"));

	}

}
