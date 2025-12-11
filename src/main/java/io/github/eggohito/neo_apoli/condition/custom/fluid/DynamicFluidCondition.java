package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicFluidCondition(BooleanProvider value) implements FluidCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicFluidCondition> CODEC = DynamicMetaCondition.createCodec(DynamicFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicFluidCondition> STREAM_CODEC = DynamicMetaCondition.createStreamCodec(DynamicFluidCondition::new);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.DYNAMIC;
	}

	@Override
	public String asDisplayString() {
		return FluidCondition.super.asDisplayString();
	}

}
