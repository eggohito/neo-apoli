package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record NumberStringProvider(NumberProvider number) implements StringProvider {

	public static final MapCodec<NumberStringProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NumberProvider.CODEC.fieldOf("number").forGetter(NumberStringProvider::number))
		.apply(instance, NumberStringProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, NumberStringProvider> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, NumberStringProvider::number,
		NumberStringProvider::new
	);

	@Override
	public @NotNull StringProviderType<?> getType() {
		return StringProviderTypes.NUMBER;
	}

	@Override
	public @NotNull String nextString(Context context) {
		return number().nextNumber(context.forChild(".number")).toString();
	}

	@Override
	public void validate(Context.Validator validator) {
		StringProvider.super.validate(validator);
		number().validate(validator.forChild(".number"));
	}

}
