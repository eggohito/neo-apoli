package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEffectConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfEffectCondition(List<EffectCondition> conditions) implements EffectCondition, AnyOfMetaCondition<EffectCondition> {

	public static final MapCodec<AnyOfEffectCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfEffectCondition.class.getSimpleName(), () -> AnyOfMetaCondition.mapCodec(EffectCondition.CODEC, AnyOfEffectCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfEffectCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfEffectCondition.class.getSimpleName(), () -> AnyOfMetaCondition.streamCodec(EffectCondition.STREAM_CODEC, AnyOfEffectCondition::new));

	@Override
	public EffectCondition.Type<?> getType() {
		return NeoApoliEffectConditionTypes.ANY_OF;
	}

}
