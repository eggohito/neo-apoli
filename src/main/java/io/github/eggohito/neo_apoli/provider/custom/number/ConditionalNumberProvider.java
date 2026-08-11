package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalNumberProvider(Condition condition, NumberProvider onTrue, NumberProvider onFalse) implements NumberProvider, ConditionalValueProvider<NumberProvider> {

	public static final MapCodec<ConditionalNumberProvider> CODEC = MapCodecUtil.lazy(ConditionalNumberProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(NumberProvider.CODEC, ConditionalNumberProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalNumberProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(NumberProvider.STREAM_CODEC, ConditionalNumberProvider::new));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.CONDITIONAL;
	}

	@Override
	public double getDouble(Context context) {
		return this.getValue(context, NumberProvider::getDouble, 0.0);
	}

}
