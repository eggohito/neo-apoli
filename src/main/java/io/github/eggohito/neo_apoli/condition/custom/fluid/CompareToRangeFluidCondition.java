package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliFluidConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeFluidCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements FluidCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeFluidCondition> MAP_CODEC = CompareToRangeMetaCondition.mapCodec(CompareToRangeFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeFluidCondition> STREAM_CODEC = CompareToRangeMetaCondition.streamCodec(CompareToRangeFluidCondition::new);

	@Override
	public FluidCondition.Type<?> getType() {
		return NeoApoliFluidConditionTypes.COMPARE_TO_RANGE;
	}

}
