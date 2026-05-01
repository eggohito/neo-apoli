package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.condition.kind.custom.FluidConditionKind;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceFluidCondition(ResourceLocation value) implements FluidCondition, ReferenceMetaCondition<FluidCondition> {

	public static final MapCodec<ReferenceFluidCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceFluidCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceFluidCondition::new);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.REFERENCE;
	}

	@Override
	public ConditionKind<FluidCondition> targetKind() {
		return FluidConditionKind.INSTANCE;
	}

}
