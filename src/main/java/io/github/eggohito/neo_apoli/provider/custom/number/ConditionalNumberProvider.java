package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalNumberProvider(Condition condition, NumberProvider ifValue, NumberProvider elseValue) implements NumberProvider, ConditionalValueProvider<NumberProvider> {

	public static final MapCodec<ConditionalNumberProvider> MAP_CODEC = MapCodecUtil.lazy(ConditionalNumberProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(NumberProvider.CODEC, ConditionalNumberProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalNumberProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(NumberProvider.STREAM_CODEC, ConditionalNumberProvider::new));

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.CONDITIONAL;
	}

	@Override
	public @NotNull Number nextNumber(Context context) {
		return this.nextOrElse(context, NumberProvider::nextNumber, () -> 0.0D);
	}

}
