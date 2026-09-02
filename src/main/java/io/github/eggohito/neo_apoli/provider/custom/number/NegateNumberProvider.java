package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record NegateNumberProvider(NumberProvider number) implements NumberProvider {

	public static final MapCodec<NegateNumberProvider> CODEC = MapCodecUtil.lazy(NegateNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance
		.group(NumberProvider.CODEC.fieldOf("number").forGetter(NegateNumberProvider::number))
		.apply(instance, NegateNumberProvider::new)
	));

	public static final StreamCodec<RegistryFriendlyByteBuf, NegateNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(NegateNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		NumberProvider.STREAM_CODEC, NegateNumberProvider::number,
		NegateNumberProvider::new
	));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.NEGATE;
	}

	@Override
	public double getDouble(Context context) {
		return -number().getDouble(context.forChild(".number"));
	}

	@Override
	public long getLong(Context context) {

		try {
			return Math.negateExact(number().getLong(context.forChild(".number")));
		}

		catch (ArithmeticException e) {
			context.reportProblem(e.getMessage());
		}

		return 0L;

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		number().validate(validator.forChild(".number"));
	}

}
