package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfFluidCondition(List<FluidCondition> conditions) implements FluidCondition, AllOfMetaCondition<FluidCondition> {

	public static final MapCodec<AllOfFluidCondition> CODEC = MapCodecUtil.lazy(AllOfFluidCondition.class.getSimpleName(), () -> AllOfMetaCondition.createCodec(FluidCondition.CODEC, AllOfFluidCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfFluidCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfFluidCondition.class.getSimpleName(), () -> AllOfMetaCondition.createStreamCodec(FluidCondition.STREAM_CODEC, AllOfFluidCondition::new));

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.ALL_OF;
	}

	@Override
	public String asDisplayString() {
		return FluidCondition.super.asDisplayString();
	}

}
