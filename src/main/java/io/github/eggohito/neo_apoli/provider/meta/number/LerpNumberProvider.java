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
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;

@EqualsAndHashCode
@Data
public class LerpNumberProvider extends NumberProvider {

	public static final MapCodec<LerpNumberProvider> CODEC = MapCodecUtil.lazy(LerpNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("delta").forGetter(LerpNumberProvider::delta),
		NumberProvider.CODEC.fieldOf("start").forGetter(LerpNumberProvider::start),
		NumberProvider.CODEC.fieldOf("end").forGetter(LerpNumberProvider::end)
	).apply(instance, LerpNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, LerpNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(LerpNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, LerpNumberProvider::delta,
		NumberProvider.PACKET_CODEC, LerpNumberProvider::start,
		NumberProvider.PACKET_CODEC, LerpNumberProvider::end,
		LerpNumberProvider::new
	));

	private final NumberProvider delta;
	private final NumberProvider start;
	private final NumberProvider end;

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.LERP;
	}

	@Override
	protected Number impl(Context context) {
		return lerp(context, NumberProvider::nextDouble, MathHelper::lerp);
	}

	@Override
	protected long longImpl(Context context) {
		return lerp(context, NumberProvider::nextInt, MathHelper::lerp);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		delta().validate(reporter.makeChild(".delta"));
		start().validate(reporter.makeChild(".start"));
		end().validate(reporter.makeChild(".end"));

	}

	private <N extends Number> N lerp(Context context, BiFunction<NumberProvider, Context, N> getter, TriFunction<Float, N, N, N> lerper) {

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
			return lerper.apply(delta, start, end);
		}

	}

}
