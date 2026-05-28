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

public record QuotientNumberProvider(NumberProvider dividend, NumberProvider divisor) implements NumberProvider {

	public static final MapCodec<QuotientNumberProvider> CODEC = MapCodecUtil.lazy(QuotientNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("dividend").forGetter(QuotientNumberProvider::dividend),
		NumberProvider.CODEC.fieldOf("divisor").forGetter(QuotientNumberProvider::divisor)
	).apply(instance, QuotientNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, QuotientNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(QuotientNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, QuotientNumberProvider::dividend,
		NumberProvider.STREAM_CODEC, QuotientNumberProvider::divisor,
		QuotientNumberProvider::new
	));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.QUOTIENT;
	}

	@Override
	public double getDouble(Context context) {

		Context dividendContext = context.forChild(".dividend");
		double dividend = dividend().getDouble(dividendContext);

		if (dividendContext.hasErrors()) {
			return 0.0d;
		}

		Context divisorContext = context.forChild(".divisor");
		double divisor = divisor().getDouble(divisorContext);

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
