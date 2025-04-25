package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.type.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Locale;

public record NumberStringProvider(NumberProvider number, NumberProvider decimals) implements StringProvider {

	public static final MapCodec<NumberStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(NumberStringProvider::number),
		NumberProvider.CODEC.optionalFieldOf("decimals", new ConstantNumberProvider(0)).forGetter(NumberStringProvider::decimals)
	).apply(instance, NumberStringProvider::new));

	public static final PacketCodec<RegistryByteBuf, NumberStringProvider> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, NumberStringProvider::number,
		NumberProvider.PACKET_CODEC, NumberStringProvider::decimals,
		NumberStringProvider::new
	);

	@Override
	public Type<?> getType() {
		return StringProviderTypes.NUMBER;
	}

	@Override
	public String get(Context context) {

		Number number = number().get(context);
		int decimals = decimals().get(context).intValue();

		if (decimals == 0) {
			return Long.toString(number.longValue());
		}

		else {
			return String.format(Locale.ROOT, ("%." + decimals + "f"), number.doubleValue());
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		number().validate(reporter.makeChild("number"));
		decimals().validate(reporter.makeChild("decimals"));
	}

}
