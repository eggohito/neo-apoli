package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEffectConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedEffectCondition(EffectCondition condition) implements EffectCondition, InvertedMetaCondition<EffectCondition> {

	public static final MapCodec<InvertedEffectCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedEffectCondition.class.getSimpleName(), () -> InvertedMetaCondition.mapCodec(EffectCondition.CODEC, InvertedEffectCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedEffectCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedEffectCondition.class.getSimpleName(), () -> InvertedMetaCondition.streamCodec(EffectCondition.STREAM_CODEC, InvertedEffectCondition::new));

	@Override
	public EffectCondition.Type<?> getType() {
		return NeoApoliEffectConditionTypes.INVERTED;
	}

}
