package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfEffectCondition(List<EffectCondition> conditions) implements EffectCondition, AllOfMetaCondition<EffectCondition> {

	public static final MapCodec<AllOfEffectCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfEffectCondition.class.getSimpleName(), () -> AllOfMetaCondition.mapCodec(EffectCondition.CODEC, AllOfEffectCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfEffectCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfEffectCondition.class.getSimpleName(), () -> AllOfMetaCondition.streamCodec(EffectCondition.STREAM_CODEC, AllOfEffectCondition::new));

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.ALL_OF;
	}

}
