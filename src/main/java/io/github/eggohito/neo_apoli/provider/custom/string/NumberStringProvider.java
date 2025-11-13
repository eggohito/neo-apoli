package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import joptsimple.internal.Strings;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Locale;

public record NumberStringProvider(NumberProvider number, NumberProvider decimals) implements StringProvider {

	public static final MapCodec<NumberStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(NumberStringProvider::number),
		NumberProvider.CODEC.fieldOf("decimals").forGetter(NumberStringProvider::decimals)
	).apply(instance, NumberStringProvider::new));

	public static final PacketCodec<RegistryByteBuf, NumberStringProvider> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, NumberStringProvider::number,
		NumberProvider.PACKET_CODEC, NumberStringProvider::decimals,
		NumberStringProvider::new
	);

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.NUMBER;
	}

	@Override
	public @NotNull String next(Context context) {

		Context numberContext = context.makeChild(".number");
		int decimals = decimals().nextInt(context.makeChild(".decimals"));

		if (decimals == 0) {
			return Long.toString(number().nextLong(numberContext));
		}

		else {
			return new DecimalFormat("#." + Strings.repeat('#', decimals)).format(number().nextDouble(numberContext));
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {

		StringProvider.super.validate(reporter);

		number().validate(reporter.makeChild(".number"));
		decimals().validate(reporter.makeChild(".decimals"));

	}

}
