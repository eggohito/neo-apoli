package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfFluidCondition(List<FluidCondition> conditions) implements FluidCondition, IAnyOfMetaCondition<FluidCondition> {

	public static final MapCodec<AnyOfFluidCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfFluidCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.mapCodec(FluidCondition.CODEC, AnyOfFluidCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfFluidCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfFluidCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.streamCodec(FluidCondition.STREAM_CODEC, AnyOfFluidCondition::new));

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.ANY_OF;
	}

}
