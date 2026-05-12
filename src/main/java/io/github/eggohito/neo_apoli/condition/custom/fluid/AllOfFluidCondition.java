package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliFluidConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfFluidCondition(List<FluidCondition> conditions) implements FluidCondition, AllOfMetaCondition<FluidCondition> {

	public static final MapCodec<AllOfFluidCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfFluidCondition.class.getSimpleName(), () -> AllOfMetaCondition.mapCodec(FluidCondition.CODEC, AllOfFluidCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfFluidCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfFluidCondition.class.getSimpleName(), () -> AllOfMetaCondition.streamCodec(FluidCondition.STREAM_CODEC, AllOfFluidCondition::new));

	@Override
	public FluidCondition.Type<?> getType() {
		return NeoApoliFluidConditionTypes.ALL_OF;
	}

}
