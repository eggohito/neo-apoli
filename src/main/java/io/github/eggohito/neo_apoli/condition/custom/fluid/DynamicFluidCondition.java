package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliFluidConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicFluidCondition(BooleanProvider value) implements FluidCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicFluidCondition> MAP_CODEC = DynamicMetaCondition.mapCodec(DynamicFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicFluidCondition> STREAM_CODEC = DynamicMetaCondition.streamCodec(DynamicFluidCondition::new);

	@Override
	public FluidCondition.Type<?> getType() {
		return NeoApoliFluidConditionTypes.DYNAMIC;
	}

}
