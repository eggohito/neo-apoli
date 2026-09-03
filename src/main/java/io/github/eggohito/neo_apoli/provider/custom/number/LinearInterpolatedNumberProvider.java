package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public record LinearInterpolatedNumberProvider(NumberProvider delta, NumberProvider start, NumberProvider end) implements NumberProvider {

	public static final MapCodec<LinearInterpolatedNumberProvider> CODEC = MapCodecUtil.lazy(LinearInterpolatedNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("delta").forGetter(LinearInterpolatedNumberProvider::delta),
		NumberProvider.CODEC.fieldOf("start").forGetter(LinearInterpolatedNumberProvider::start),
		NumberProvider.CODEC.fieldOf("end").forGetter(LinearInterpolatedNumberProvider::end)
	).apply(instance, LinearInterpolatedNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, LinearInterpolatedNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(LinearInterpolatedNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, LinearInterpolatedNumberProvider::delta,
		NumberProvider.STREAM_CODEC, LinearInterpolatedNumberProvider::start,
		NumberProvider.STREAM_CODEC, LinearInterpolatedNumberProvider::end,
		LinearInterpolatedNumberProvider::new
	));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.LINEAR_INTERPOLATED;
	}

	@Override
	public double getDouble(Context context) {
		return this.lerp(context, NumberProvider::getDouble, Mth::lerp);
	}

	@Override
	public long getLong(Context context) {
		return this.lerp(context, NumberProvider::getInt, Mth::lerpInt);
	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		delta().validate(validator.forChild(".delta"));
		start().validate(validator.forChild(".start"));
		end().validate(validator.forChild(".end"));

	}

	private <N extends Number> N lerp(Context context, BiFunction<NumberProvider, Context, N> getter, TriFunction<Float, N, N, N> interpolator) {

		Context startContext = context.forChild(".start");
		N start = getter.apply(start(), startContext);

		Context deltaContext = context.forChild(".delta");
		float delta = delta().getFloat(deltaContext);

		if (deltaContext.hasProblems()) {
			return start;
		}

		Context endContext = context.forChild(".end");
		N end = getter.apply(end(), endContext);

		if (endContext.hasProblems()) {
			return start;
		}

		return interpolator.apply(delta, start, end);

	}

}
