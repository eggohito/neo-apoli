package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeBiEntityCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements BiEntityCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeBiEntityCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeBiEntityCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeBiEntityCondition::new);

	@Override
	public BiEntityCondition.Type<?> getType() {
		return NeoApoliBiEntityConditionTypes.COMPARE_TO_RANGE;
	}

}
