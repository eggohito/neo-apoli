package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliFluidConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedFluidCondition(FluidCondition condition) implements FluidCondition, InvertedMetaCondition<FluidCondition> {

	public static final MapCodec<InvertedFluidCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedFluidCondition.class.getSimpleName(), () -> InvertedMetaCondition.mapCodec(FluidCondition.CODEC, InvertedFluidCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedFluidCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedFluidCondition.class.getSimpleName(), () -> InvertedMetaCondition.streamCodec(FluidCondition.STREAM_CODEC, InvertedFluidCondition::new));

	@Override
	public FluidCondition.Type<?> getType() {
		return NeoApoliFluidConditionTypes.INVERTED;
	}

}
