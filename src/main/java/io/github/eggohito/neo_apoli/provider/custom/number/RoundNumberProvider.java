package io.github.eggohito.neo_apoli.provider.custom.number;

import com.google.common.base.Strings;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Optional;

public record RoundNumberProvider(NumberProvider number, NumberProvider places, Optional<RoundingMode> mode) implements NumberProvider {

	public static final MapCodec<RoundNumberProvider> MAP_CODEC = MapCodecUtil.lazy(RoundNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(RoundNumberProvider::number),
		NumberProvider.CODEC.fieldOf("places").forGetter(RoundNumberProvider::places),
		NeoApoliCodecs.ROUNDING_MODE.optionalFieldOf("mode").forGetter(RoundNumberProvider::mode)
	).apply(instance, RoundNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, RoundNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(RoundNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, RoundNumberProvider::number,
		NumberProvider.STREAM_CODEC, RoundNumberProvider::places,
		ByteBufCodecs.optional(NeoApoliStreamCodecs.ROUNDING_MODE), RoundNumberProvider::mode,
		RoundNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ROUND;
	}

	@Override
	public @NotNull Number next(Context context) {

		double number = number().nextDouble(context.forChild(".number"));
		int places = Math.abs(places().nextInt(context.forChild(".places")));

		DecimalFormat decimalFormat = new DecimalFormat("#." + Strings.repeat("#", Math.max(places, 1)));
		mode().ifPresent(decimalFormat::setRoundingMode);

		try {
			return Double.parseDouble(decimalFormat.format(number));
		}

		catch (ArithmeticException e) {
			context.reportProblem("Couldn't round number " + number + " to " + places + " decimal places: " + e.getMessage());
		}

		return number;

	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		number().validate(validator.forChild(".number"));
		places().validate(validator.forChild(".places"));

	}

}
