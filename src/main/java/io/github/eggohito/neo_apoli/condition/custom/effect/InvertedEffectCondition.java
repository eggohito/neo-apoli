package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IInvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedEffectCondition(EffectCondition condition) implements EffectCondition, IInvertedMetaCondition<EffectCondition> {

	public static final MapCodec<InvertedEffectCondition> CODEC = MapCodecUtil.lazy(InvertedEffectCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createCodec(EffectCondition.CODEC, InvertedEffectCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedEffectCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedEffectCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createStreamCodec(EffectCondition.STREAM_CODEC, InvertedEffectCondition::new));

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return EffectCondition.super.asDisplayString();
	}

}
