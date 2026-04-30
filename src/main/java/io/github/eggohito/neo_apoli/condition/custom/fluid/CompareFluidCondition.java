package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.comparison.Comparison;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareFluidCondition(Comparison comparison) implements FluidCondition, CompareMetaCondition {

	public static final MapCodec<CompareFluidCondition> MAP_CODEC = CompareMetaCondition.mapCodec(CompareFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareFluidCondition> STREAM_CODEC = CompareMetaCondition.streamCodec(CompareFluidCondition::new);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.COMPARE;
	}

}
