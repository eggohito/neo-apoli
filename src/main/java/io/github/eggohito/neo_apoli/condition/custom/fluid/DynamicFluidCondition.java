package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IDynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicFluidCondition(BooleanProvider value) implements FluidCondition, IDynamicMetaCondition {

	public static final MapCodec<DynamicFluidCondition> MAP_CODEC = IDynamicMetaCondition.mapCodec(DynamicFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicFluidCondition> STREAM_CODEC = IDynamicMetaCondition.streamCodec(DynamicFluidCondition::new);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.DYNAMIC;
	}

}
