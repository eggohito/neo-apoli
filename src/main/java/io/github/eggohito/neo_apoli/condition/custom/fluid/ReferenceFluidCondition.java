package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceFluidCondition(ResourceLocation value) implements FluidCondition, IReferenceMetaCondition<FluidCondition> {

	public static final MapCodec<ReferenceFluidCondition> CODEC = IReferenceMetaCondition.createCodec(ReferenceFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceFluidCondition> STREAM_CODEC = IReferenceMetaCondition.createStreamCodec(ReferenceFluidCondition::new);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.REFERENCE;
	}

	@Override
	public Pair<Class<FluidCondition>, String> classAndName() {
		return Pair.of(FluidCondition.class, "Fluid condition");
	}

	@Override
	public String asDisplayString() {
		return FluidCondition.super.asDisplayString();
	}

}
