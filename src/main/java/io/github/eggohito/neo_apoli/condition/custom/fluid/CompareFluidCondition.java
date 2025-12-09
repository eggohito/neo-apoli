package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareFluidCondition(Comparison comparison) implements FluidCondition, CompareMetaCondition {

	public static final MapCodec<CompareFluidCondition> CODEC = CompareMetaCondition.createCodec(CompareFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareFluidCondition> STREAM_CODEC = CompareMetaCondition.createStreamCodec(CompareFluidCondition::new);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.COMPARE;
	}

	@Override
	public String asDisplayString() {
		return FluidCondition.super.asDisplayString();
	}

}
