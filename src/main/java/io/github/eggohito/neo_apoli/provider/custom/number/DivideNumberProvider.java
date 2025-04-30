package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record DivideNumberProvider(NumberProvider dividend, NumberProvider divisor) implements NumberProvider {

	public static final MapCodec<DivideNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("dividend").forGetter(DivideNumberProvider::dividend),
		NumberProvider.CODEC.fieldOf("divisor").forGetter(DivideNumberProvider::divisor)
	).apply(instance, DivideNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, DivideNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, DivideNumberProvider::dividend,
		NumberProvider.PACKET_CODEC, DivideNumberProvider::divisor,
		DivideNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DIVIDE;
	}

	@Override
	public double doubleValue(Context context) {
		return dividend().doubleValue(context.makeChild("dividend")) / divisor().doubleValue(context.makeChild("divisor"));
	}

	@Override
	public long longValue(Context context) {
		return dividend().longValue(context.makeChild("dividend")) / divisor().longValue(context.makeChild("divisor"));
	}

	@Override
	public void validate(ErrorReporter reporter) {
		dividend().validate(reporter.makeChild("dividend"));
		divisor().validate(reporter.makeChild("divisor"));
	}

}
