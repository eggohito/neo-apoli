package io.github.eggohito.neo_apoli.provider.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.meta.SwitchValueProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliEffectProviderTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;
import java.util.Optional;

public record SwitchEffectProvider(List<Case<Condition, EffectProvider>> cases, EffectProvider defaultValue) implements EffectProvider, SwitchValueProvider<EffectProvider> {

	public static final MapCodec<SwitchEffectProvider> CODEC = MapCodecUtil.lazy(SwitchEffectProvider.class.getSimpleName(), () -> SwitchValueProvider.mapCodec(EffectProvider.CODEC, SwitchEffectProvider::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchEffectProvider> STREAM_CODEC = StreamCodecUtil.lazy(SwitchEffectProvider.class.getSimpleName(), () -> SwitchValueProvider.streamCodec(EffectProvider.STREAM_CODEC, SwitchEffectProvider::new));

	@Override
	public EffectProvider.Type<?> getType() {
		return NeoApoliEffectProviderTypes.SWITCH;
	}

	@Override
	public Optional<MobEffectInstance> nextEffect(Context context) {
		return this.nextOrDefault(context, EffectProvider::nextEffect);
	}

}
