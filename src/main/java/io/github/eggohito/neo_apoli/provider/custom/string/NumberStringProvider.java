package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record NumberStringProvider(NumberProvider number, NumberProvider decimals) implements StringProvider {

	public static final MapCodec<NumberStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(NumberStringProvider::number),
		NumberProvider.CODEC.fieldOf("decimals").forGetter(NumberStringProvider::decimals)
	).apply(instance, NumberStringProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, NumberStringProvider> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, NumberStringProvider::number,
		NumberProvider.STREAM_CODEC, NumberStringProvider::decimals,
		NumberStringProvider::new
	);

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.NUMBER;
	}

	@Override
	public @NotNull String next(Context context) {

		Context numberContext = context.forChild(".number");
		int decimals = decimals().nextInt(context.forChild(".decimals"));

		if (decimals == 0) {
			return Long.toString(number().nextLong(numberContext));
		}

		else {
			return MiscUtil.decimalPlacesFormat(decimals).format(number().nextDouble(numberContext));
		}

	}

	@Override
	public void validate(Context.Validator validator) {

		StringProvider.super.validate(validator);

		number().validate(validator.forChild(".number"));
		decimals().validate(validator.forChild(".decimals"));

	}

}
