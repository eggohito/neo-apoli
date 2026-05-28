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

public record AbsoluteNumberProvider(NumberProvider number) implements NumberProvider {

	public static final MapCodec<AbsoluteNumberProvider> CODEC = MapCodecUtil.lazy(AbsoluteNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("number").forGetter(AbsoluteNumberProvider::number)
	).apply(instance, AbsoluteNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, AbsoluteNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(AbsoluteNumberProvider.class.getSimpleName(), () -> NumberProvider.STREAM_CODEC.map(
		AbsoluteNumberProvider::new,
		AbsoluteNumberProvider::number
	));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ABSOLUTE;
	}

	@Override
	public double getDouble(Context context) {
		return Math.abs(this.number().getDouble(context.forChild(".number")));
	}

	@Override
	public long getLong(Context context) {
		return Math.abs(this.number().getLong(context.forChild(".number")));
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		number().validate(validator.forChild(".number"));
	}

}
