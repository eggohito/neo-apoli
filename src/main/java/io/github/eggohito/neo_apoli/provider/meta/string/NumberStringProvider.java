package io.github.eggohito.neo_apoli.provider.meta.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.meta.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Locale;

@EqualsAndHashCode(callSuper = false)
@Data
public final class NumberStringProvider extends StringProvider {

	public static final MapCodec<NumberStringProvider> CODEC = NeoApoliMapCodecs.lazy(NumberStringProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(NumberStringProvider::number),
		NumberProvider.CODEC.optionalFieldOf("decimals", new ConstantNumberProvider(0)).forGetter(NumberStringProvider::decimals)
	).apply(instance, NumberStringProvider::new)));

	public static final PacketCodec<RegistryByteBuf, NumberStringProvider> PACKET_CODEC = NeoApoliPacketCodecs.lazy(NumberStringProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, NumberStringProvider::number,
		NumberProvider.PACKET_CODEC, NumberStringProvider::decimals,
		NumberStringProvider::new
	));

	private final NumberProvider number;
	private final NumberProvider decimals;

	public NumberStringProvider(NumberProvider number, NumberProvider decimals) {
		this.number = number;
		this.decimals = decimals;
	}

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.NUMBER;
	}

	@Override
	protected String impl(Context context) {

		Context numberContext = context.makeChild(".number");
		int decimals = decimals().nextInt(context.makeChild(".decimals"));

		if (decimals == 0) {
			return Long.toString(number().nextLong(numberContext));
		}

		else {
			return String.format(Locale.ROOT, ("%." + decimals + "f"), number().nextDouble(numberContext));
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		number().validate(reporter.makeChild(".number"));
		decimals().validate(reporter.makeChild(".decimals"));

	}

}
