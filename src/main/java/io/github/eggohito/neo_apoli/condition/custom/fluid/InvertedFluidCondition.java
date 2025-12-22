package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IInvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedFluidCondition(FluidCondition condition) implements FluidCondition, IInvertedMetaCondition<FluidCondition> {

	public static final MapCodec<InvertedFluidCondition> CODEC = MapCodecUtil.lazy(InvertedFluidCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createCodec(FluidCondition.CODEC, InvertedFluidCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedFluidCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedFluidCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createStreamCodec(FluidCondition.STREAM_CODEC, InvertedFluidCondition::new));

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return FluidCondition.super.asDisplayString();
	}

}
