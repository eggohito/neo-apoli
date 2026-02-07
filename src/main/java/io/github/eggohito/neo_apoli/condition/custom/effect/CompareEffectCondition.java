package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareEffectCondition(Comparison comparison) implements EffectCondition, ICompareMetaCondition {

	public static final MapCodec<CompareEffectCondition> MAP_CODEC = ICompareMetaCondition.mapCodec(CompareEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareEffectCondition> STREAM_CODEC = ICompareMetaCondition.streamCodec(CompareEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.COMPARE;
	}

}
