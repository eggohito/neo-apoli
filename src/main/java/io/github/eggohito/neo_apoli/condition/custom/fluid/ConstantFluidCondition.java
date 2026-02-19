package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantFluidCondition(boolean value) implements FluidCondition, ConstantMetaCondition {

	public static final Codec<ConstantFluidCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantFluidCondition::new);

	public static final MapCodec<ConstantFluidCondition> MAP_CODEC = ConstantMetaCondition.mapCodec(ConstantFluidCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantFluidCondition> STREAM_CODEC = ConstantMetaCondition.streamCodec(ConstantFluidCondition::new);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.CONSTANT;
	}

}
