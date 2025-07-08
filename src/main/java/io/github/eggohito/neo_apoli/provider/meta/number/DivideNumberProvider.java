package io.github.eggohito.neo_apoli.provider.meta.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class DivideNumberProvider extends NumberProvider {

	public static final MapCodec<DivideNumberProvider> CODEC = MapCodecUtil.lazy(DivideNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("dividend").forGetter(DivideNumberProvider::dividend),
		NumberProvider.CODEC.fieldOf("divisor").forGetter(DivideNumberProvider::divisor)
	).apply(instance, DivideNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, DivideNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(DivideNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, DivideNumberProvider::dividend,
		NumberProvider.PACKET_CODEC, DivideNumberProvider::divisor,
		DivideNumberProvider::new
	));

	private final NumberProvider dividend;
	private final NumberProvider divisor;

	public DivideNumberProvider(NumberProvider dividend, NumberProvider divisor) {
		this.dividend = dividend;
		this.divisor = divisor;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DIVIDE;
	}

	@Override
	protected Number impl(Context context) {

		Context dividendContext = context.makeChild(".dividend");
		double dividend = dividend().nextDouble(dividendContext);

		Context divisorContext = context.makeChild(".divisor");
		double divisor = divisor().nextDouble(divisorContext);

		if (dividendContext.hasErrors() || divisorContext.hasErrors()) {
			return 0.0D;
		}

		else {
			return dividend / divisor;
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		dividend().validate(reporter.makeChild(".dividend"));
		divisor().validate(reporter.makeChild(".divisor"));

	}

}
