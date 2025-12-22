package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeBiEntityCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements BiEntityCondition, ICompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeBiEntityCondition> CODEC = ICompareToRangeMetaCondition.createCodec(CompareToRangeBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeBiEntityCondition> STREAM_CODEC = ICompareToRangeMetaCondition.createStreamCodec(CompareToRangeBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}
