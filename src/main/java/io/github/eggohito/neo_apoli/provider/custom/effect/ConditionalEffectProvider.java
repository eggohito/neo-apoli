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
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ConditionalEffectProvider(Condition condition, EffectProvider onTrue, EffectProvider onFalse) implements EffectProvider, ConditionalValueProvider<EffectProvider> {

	public static final MapCodec<ConditionalEffectProvider> CODEC = MapCodecUtil.lazy(ConditionalEffectProvider.class.getSimpleName(), () -> ConditionalValueProvider.mapCodec(EffectProvider.CODEC, ConditionalEffectProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalEffectProvider> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalEffectProvider.class.getSimpleName(), () -> ConditionalValueProvider.streamCodec(EffectProvider.STREAM_CODEC, ConditionalEffectProvider::new));

	@Override
	public EffectProvider.@NotNull Type<?> getType() {
		return NeoApoliEffectProviderTypes.CONDITIONAL;
	}

	@Override
	public Optional<MobEffectInstance> getEffect(Context context) {
		return this.getValue(context, EffectProvider::getEffect, Optional.empty());
	}

}
