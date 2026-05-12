package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeCondition::new);

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.COMPARE_TO_RANGE;
	}

}
