package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ICompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CompareToRangeFluidCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements FluidCondition, ICompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeFluidCondition> CODEC = ICompareToRangeMetaCondition.createCodec(CompareToRangeFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareToRangeFluidCondition> STREAM_CODEC = ICompareToRangeMetaCondition.createStreamCodec(CompareToRangeFluidCondition::new);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return FluidCondition.super.asDisplayString();
	}

}
