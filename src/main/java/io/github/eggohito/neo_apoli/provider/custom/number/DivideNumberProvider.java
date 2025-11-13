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
import org.jetbrains.annotations.NotNull;

public record DivideNumberProvider(NumberProvider dividend, NumberProvider divisor) implements NumberProvider {

	public static final MapCodec<DivideNumberProvider> CODEC = MapCodecUtil.lazy(DivideNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("dividend").forGetter(DivideNumberProvider::dividend),
		NumberProvider.CODEC.fieldOf("divisor").forGetter(DivideNumberProvider::divisor)
	).apply(instance, DivideNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, DivideNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(DivideNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, DivideNumberProvider::dividend,
		NumberProvider.PACKET_CODEC, DivideNumberProvider::divisor,
		DivideNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DIVIDE;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context dividendContext = context.makeChild(".dividend");
		double dividend = dividend().nextDouble(dividendContext);

		if (dividendContext.hasErrors()) {
			return 0.0d;
		}

		Context divisorContext = context.makeChild(".divisor");
		double divisor = divisor().nextDouble(divisorContext);

		if (divisorContext.hasErrors()) {
			return 0.0d;
		}

		return dividend / divisor;

	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);

		dividend().validate(reporter.makeChild(".dividend"));
		divisor().validate(reporter.makeChild(".divisor"));

	}

}
