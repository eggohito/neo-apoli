package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.comparison.Comparison;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareEffectCondition(Comparison comparison) implements EffectCondition, CompareMetaCondition {

	public static final MapCodec<CompareEffectCondition> MAP_CODEC = CompareMetaCondition.mapCodec(CompareEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareEffectCondition> STREAM_CODEC = CompareMetaCondition.streamCodec(CompareEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.COMPARE;
	}

}
