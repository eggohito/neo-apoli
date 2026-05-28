package io.github.eggohito.neo_apoli.provider.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.ConditionalValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliEffectProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;

public record ConditionalEffectProvider(Condition condition, EffectProvider ifValue, EffectProvider elseValue) implements EffectProvider, ConditionalValueProvider<EffectProvider> {

	public static final MapCodec<ConditionalEffectProvider> CODEC = MapCodecUtil.lazy(ConditionalEffectProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(EffectProvider.CODEC, ConditionalEffectProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalEffectProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalEffectProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(EffectProvider.STREAM_CODEC, ConditionalEffectProvider::new));

	@Override
	public EffectProvider.Type<?> getType() {
		return NeoApoliEffectProviderTypes.CONDITIONAL;
	}

	@Override
	public Optional<MobEffectInstance> nextEffect(Context context) {
		return this.nextOrElse(context, EffectProvider::nextEffect, Optional::empty);
	}

}
