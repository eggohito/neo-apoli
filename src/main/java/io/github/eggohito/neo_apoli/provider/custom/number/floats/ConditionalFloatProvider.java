package io.github.eggohito.neo_apoli.provider.custom.number.floats;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliFloatProviderTypes;
import io.github.eggohito.neo_apoli.util.FloatConsumer;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ConditionalFloatProvider(Condition condition, FloatProvider onTrue, FloatProvider onFalse) implements FloatProvider, ConditionalValueProvider<FloatProvider> {

	public static final MapCodec<ConditionalFloatProvider> CODEC = MapCodecUtil.lazy(ConditionalFloatProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(FloatProvider.CODEC, ConditionalFloatProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalFloatProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalFloatProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(FloatProvider.STREAM_CODEC, ConditionalFloatProvider::new));

	@Override
	public @NotNull FloatProvider.Type<?> getType() {
		return NeoApoliFloatProviderTypes.CONDITIONAL;
	}

	@Override
	public void provideFloat(Context context, FloatConsumer setter) {
		this.select(context).ifPresent(selected -> selected.provider().provideFloat(selected.context(), setter));
	}

}
