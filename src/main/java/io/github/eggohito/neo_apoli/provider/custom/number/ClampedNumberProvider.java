package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.MathHelper;

public record ClampedNumberProvider(NumberProvider value, NumberProvider min, NumberProvider max) implements NumberProvider {

	public static final MapCodec<ClampedNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("value").forGetter(ClampedNumberProvider::value),
		NumberProvider.CODEC.fieldOf("min").forGetter(ClampedNumberProvider::min),
		NumberProvider.CODEC.fieldOf("max").forGetter(ClampedNumberProvider::max)
	).apply(instance, ClampedNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, ClampedNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, ClampedNumberProvider::value,
		NumberProvider.PACKET_CODEC, ClampedNumberProvider::min,
		NumberProvider.PACKET_CODEC, ClampedNumberProvider::max,
		ClampedNumberProvider::new
	);

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.CLAMPED;
	}

	@Override
	public Number get(Context context) {

		double value = value().get(context.makeChild("value")).doubleValue();

		double min = min().get(context.makeChild("min")).doubleValue();
		double max = max().get(context.makeChild("max")).doubleValue();

		return MathHelper.clamp(value, min, max);

	}

	@Override
	public void validate(ErrorReporter reporter) {
		value().validate(reporter.makeChild("value"));
		min().validate(reporter.makeChild("min"));
		max().validate(reporter.makeChild("max"));
	}

}
