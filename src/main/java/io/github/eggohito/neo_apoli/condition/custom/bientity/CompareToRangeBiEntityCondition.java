package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeBiEntityCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements BiEntityCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeBiEntityCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeBiEntityCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.COMPARE_TO_RANGE;
	}

}
