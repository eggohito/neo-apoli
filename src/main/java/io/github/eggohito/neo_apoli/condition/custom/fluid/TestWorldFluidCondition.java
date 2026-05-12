package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliFluidConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldFluidCondition(WorldCondition condition) implements FluidCondition, TestWorldMetaCondition {

	public static final MapCodec<TestWorldFluidCondition> MAP_CODEC = TestWorldMetaCondition.mapCodec(TestWorldFluidCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldFluidCondition> STREAM_CODEC = TestWorldMetaCondition.streamCodec(TestWorldFluidCondition::new);

	@Override
	public FluidCondition.Type<?> getType() {
		return NeoApoliFluidConditionTypes.TEST_WORLD;
	}

}
