package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.MathHelper;

@EqualsAndHashCode
@Data
public final class ClampedNumberProvider extends NumberProvider {

	public static final MapCodec<ClampedNumberProvider> CODEC = NeoApoliMapCodecs.lazy(ClampedNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("value").forGetter(ClampedNumberProvider::value),
		NumberProvider.CODEC.fieldOf("min").forGetter(ClampedNumberProvider::min),
		NumberProvider.CODEC.fieldOf("max").forGetter(ClampedNumberProvider::max)
	).apply(instance, ClampedNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, ClampedNumberProvider> PACKET_CODEC = NeoApoliPacketCodecs.lazy(ClampedNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, ClampedNumberProvider::value,
		NumberProvider.PACKET_CODEC, ClampedNumberProvider::min,
		NumberProvider.PACKET_CODEC, ClampedNumberProvider::max,
		ClampedNumberProvider::new
	));

	private final NumberProvider value;
	private final NumberProvider min;
	private final NumberProvider max;

	public ClampedNumberProvider(NumberProvider value, NumberProvider min, NumberProvider max) {
		this.value = value;
		this.min = min;
		this.max = max;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.CLAMPED;
	}

	@Override
	protected Number impl(Context context) {

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

		super.validate(reporter);

		value().validate(reporter.makeChild(".value"));
		min().validate(reporter.makeChild(".min"));
		max().validate(reporter.makeChild(".max"));

	}

}
