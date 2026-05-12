package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliFluidConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceFluidCondition(ResourceLocation value) implements FluidCondition, ReferenceMetaCondition<FluidCondition> {

	public static final MapCodec<ReferenceFluidCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceFluidCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceFluidCondition::new);

	@Override
	public FluidCondition.Type<?> getType() {
		return NeoApoliFluidConditionTypes.REFERENCE;
	}

	@Override
	public FluidCondition.Kind targetKind() {
		return FluidCondition.Kind.INSTANCE;
	}

}
