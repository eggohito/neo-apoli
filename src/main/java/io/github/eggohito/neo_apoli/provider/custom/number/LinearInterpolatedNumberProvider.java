package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public record LinearInterpolatedNumberProvider(NumberProvider delta, NumberProvider start, NumberProvider end) implements NumberProvider {

	public static final MapCodec<LinearInterpolatedNumberProvider> CODEC = MapCodecUtil.lazy(LinearInterpolatedNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("delta").forGetter(LinearInterpolatedNumberProvider::delta),
		NumberProvider.CODEC.fieldOf("start").forGetter(LinearInterpolatedNumberProvider::start),
		NumberProvider.CODEC.fieldOf("end").forGetter(LinearInterpolatedNumberProvider::end)
	).apply(instance, LinearInterpolatedNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, LinearInterpolatedNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(LinearInterpolatedNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, LinearInterpolatedNumberProvider::delta,
		NumberProvider.PACKET_CODEC, LinearInterpolatedNumberProvider::start,
		NumberProvider.PACKET_CODEC, LinearInterpolatedNumberProvider::end,
		LinearInterpolatedNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.LINEAR_INTERPOLATED;
	}

	@Override
	public @NotNull Number next(Context context) {
		return this.lerp(context, NumberProvider::nextDouble, MathHelper::lerp);
	}

	@Override
	public long nextLong(Context context) {
		return this.lerp(context, NumberProvider::nextInt, MathHelper::lerp);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);

		delta().validate(reporter.makeChild(".delta"));
		start().validate(reporter.makeChild(".start"));
		end().validate(reporter.makeChild(".end"));

	}

	private <N extends Number> N lerp(Context context, BiFunction<NumberProvider, Context, N> getter, TriFunction<Float, N, N, N> interpolator) {

		Context deltaContext = context.makeChild(".delta");
		float delta = delta().nextFloat(deltaContext);

		Context startContext = context.makeChild(".start");
		N start = getter.apply(start(), startContext);

		Context endContext = context.makeChild(".end");
		N end = getter.apply(end(), endContext);

		if (deltaContext.hasErrors() || endContext.hasErrors()) {
			return start;
		}

		else {
			return interpolator.apply(delta, start, end);
		}

	}

}
