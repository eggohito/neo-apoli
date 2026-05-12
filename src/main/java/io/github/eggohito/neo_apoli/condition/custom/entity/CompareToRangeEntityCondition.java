package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeEntityCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements EntityCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeEntityCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeEntityCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeEntityCondition::new);

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.COMPARE_TO_RANGE;
	}

}
