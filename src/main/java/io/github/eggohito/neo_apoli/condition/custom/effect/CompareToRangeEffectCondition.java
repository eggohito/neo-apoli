package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeEffectCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements EffectCondition, ICompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeEffectCondition> MAP_CODEC = ICompareToRangeMetaCondition.mapCodec(CompareToRangeEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeEffectCondition> STREAM_CODEC = ICompareToRangeMetaCondition.streamCodec(CompareToRangeEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.COMPARE_TO_RANGE;
	}

}
