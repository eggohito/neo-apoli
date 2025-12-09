package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfEffectCondition(List<EffectCondition> conditions) implements EffectCondition, AnyOfMetaCondition<EffectCondition> {

	public static final MapCodec<AnyOfEffectCondition> CODEC = MapCodecUtil.lazy(AnyOfEffectCondition.class.getSimpleName(), () -> AnyOfMetaCondition.createCodec(EffectCondition.CODEC, AnyOfEffectCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfEffectCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfEffectCondition.class.getSimpleName(), () -> AnyOfMetaCondition.createStreamCodec(EffectCondition.STREAM_CODEC, AnyOfEffectCondition::new));

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.ANY_OF;
	}

	@Override
	public String asDisplayString() {
		return EffectCondition.super.asDisplayString();
	}

}
