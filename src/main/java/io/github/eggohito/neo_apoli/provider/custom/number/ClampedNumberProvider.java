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
import org.jetbrains.annotations.NotNull;

public record ClampedNumberProvider(NumberProvider value, NumberProvider min, NumberProvider max) implements NumberProvider {

	public static final MapCodec<ClampedNumberProvider> CODEC = MapCodecUtil.lazy(ClampedNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("value").forGetter(ClampedNumberProvider::value),
		NumberProvider.CODEC.fieldOf("min").forGetter(ClampedNumberProvider::min),
		NumberProvider.CODEC.fieldOf("max").forGetter(ClampedNumberProvider::max)
	).apply(instance, ClampedNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, ClampedNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(ClampedNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, ClampedNumberProvider::value,
		NumberProvider.PACKET_CODEC, ClampedNumberProvider::min,
		NumberProvider.PACKET_CODEC, ClampedNumberProvider::max,
		ClampedNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.CLAMPED;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context minContext = context.makeChild(".min");
		double min = min().nextDouble(minContext);

		Context maxContext = context.makeChild(".max");
		double max = max().nextDouble(maxContext);

		Context valueContext = context.makeChild(".value");
		double value = value().nextDouble(valueContext);

		if (minContext.hasErrors() || maxContext.hasErrors()) {
			return value;
		}

		else {
			return MathHelper.clamp(value, min, max);
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);

		value().validate(reporter.makeChild(".value"));
		min().validate(reporter.makeChild(".min"));
		max().validate(reporter.makeChild(".max"));

	}

}
