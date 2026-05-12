package io.github.eggohito.neo_apoli.provider.custom.number;

import com.google.common.base.Strings;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public record RoundNumberProvider(NumberProvider number, NumberProvider places, RoundingMode mode) implements NumberProvider {

	public static final MapCodec<RoundNumberProvider> MAP_CODEC = MapCodecUtil.lazy(RoundNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(RoundNumberProvider::number),
		NumberProvider.CODEC.fieldOf("places").forGetter(RoundNumberProvider::places),
		NeoApoliCodecs.ROUNDING_MODE.optionalFieldOf("mode", RoundingMode.HALF_UP).forGetter(RoundNumberProvider::mode)
	).apply(instance, RoundNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, RoundNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(RoundNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, RoundNumberProvider::number,
		NumberProvider.STREAM_CODEC, RoundNumberProvider::places,
		NeoApoliStreamCodecs.ROUNDING_MODE, RoundNumberProvider::mode,
		RoundNumberProvider::new
	));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ROUND;
	}

	@Override
	public double nextDouble(Context context) {

		double number = number().nextDouble(context.forChild(".number"));
		int places = places().nextInt(context.forChild(".places"));

		try {

			DecimalFormat decimalFormat = new DecimalFormat("#" + (places > 0 ? "." + Strings.repeat("#", places) : ""), DecimalFormatSymbols.getInstance(Locale.ROOT));
			decimalFormat.setRoundingMode(mode());

			String formatted = decimalFormat.format(number);
			return decimalFormat.parse(formatted).doubleValue();

		}

		catch (ArithmeticException | ParseException e) {
			context.reportProblem(e.getMessage());
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
