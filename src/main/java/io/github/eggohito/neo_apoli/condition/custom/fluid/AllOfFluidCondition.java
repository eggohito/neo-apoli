package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfFluidCondition(List<FluidCondition> conditions) implements FluidCondition, IAllOfMetaCondition<FluidCondition> {

	public static final MapCodec<AllOfFluidCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfFluidCondition.class.getSimpleName(), () -> IAllOfMetaCondition.mapCodec(FluidCondition.CODEC, AllOfFluidCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfFluidCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfFluidCondition.class.getSimpleName(), () -> IAllOfMetaCondition.streamCodec(FluidCondition.STREAM_CODEC, AllOfFluidCondition::new));

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.ALL_OF;
	}

}
