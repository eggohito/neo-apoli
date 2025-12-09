package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareEffectCondition(Comparison comparison) implements EffectCondition, CompareMetaCondition {

	public static final MapCodec<CompareEffectCondition> CODEC = CompareMetaCondition.createCodec(CompareEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareEffectCondition> STREAM_CODEC = CompareMetaCondition.createStreamCodec(CompareEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.COMPARE;
	}

	@Override
	public String asDisplayString() {
		return EffectCondition.super.asDisplayString();
	}

}
