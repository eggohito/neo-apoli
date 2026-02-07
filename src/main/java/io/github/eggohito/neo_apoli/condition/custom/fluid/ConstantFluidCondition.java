package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantFluidCondition(boolean value) implements FluidCondition, IConstantMetaCondition {

	public static final Codec<ConstantFluidCondition> INLINE_CODEC = IConstantMetaCondition.createInlineCodec(ConstantFluidCondition::new);

	public static final MapCodec<ConstantFluidCondition> MAP_CODEC = IConstantMetaCondition.mapCodec(ConstantFluidCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantFluidCondition> STREAM_CODEC = IConstantMetaCondition.streamCodec(ConstantFluidCondition::new);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.CONSTANT;
	}

}
