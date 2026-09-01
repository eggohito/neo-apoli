package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.CompositeConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.conditional.CompositeConditional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CompositeConditionalNumberProvider(List<CompositeConditional.Entry<NumberProvider>> entries, NumberProvider defaultValue) implements NumberProvider, CompositeConditionalValueProvider<NumberProvider> {

	public static final MapCodec<CompositeConditionalNumberProvider> CODEC = MapCodecUtil.lazy(CompositeConditionalNumberProvider.class.getSimpleName(), () -> CompositeConditionalValueProvider.mapCodec(NumberProvider.CODEC, CompositeConditionalNumberProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, CompositeConditionalNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(CompositeConditionalNumberProvider.class.getSimpleName(), () -> CompositeConditionalValueProvider.streamCodec(NumberProvider.STREAM_CODEC, CompositeConditionalNumberProvider::new));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.COMPOSITE_CONDITIONAL;
	}

	@Override
	public double getDouble(Context context) {
		return this.getOrDefault(context, NumberProvider::getDouble);
	}

}
