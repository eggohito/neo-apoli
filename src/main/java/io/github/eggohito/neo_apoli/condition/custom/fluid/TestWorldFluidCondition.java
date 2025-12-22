package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ITestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldFluidCondition(WorldCondition condition) implements FluidCondition, ITestWorldMetaCondition {

	public static final MapCodec<TestWorldFluidCondition> CODEC = ITestWorldMetaCondition.createCodec(TestWorldFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldFluidCondition> STREAM_CODEC = ITestWorldMetaCondition.createStreamCodec(TestWorldFluidCondition::new);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.TEST_WORLD;
	}

	@Override
	public String asDisplayString() {
		return FluidCondition.super.asDisplayString();
	}

}
