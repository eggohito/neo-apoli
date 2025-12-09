package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeEffectCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements EffectCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeEffectCondition> CODEC = CompareToRangeMetaCondition.createCodec(CompareToRangeEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeEffectCondition> STREAM_CODEC = CompareToRangeMetaCondition.createStreamCodec(CompareToRangeEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return EffectCondition.super.asDisplayString();
	}

}
