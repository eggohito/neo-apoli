package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfEffectCondition(List<EffectCondition> conditions) implements EffectCondition, IAnyOfMetaCondition<EffectCondition> {

	public static final MapCodec<AnyOfEffectCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfEffectCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.mapCodec(EffectCondition.CODEC, AnyOfEffectCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfEffectCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfEffectCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.streamCodec(EffectCondition.STREAM_CODEC, AnyOfEffectCondition::new));

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.ANY_OF;
	}

}
