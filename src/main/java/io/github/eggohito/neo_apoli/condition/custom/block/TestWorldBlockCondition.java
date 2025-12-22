package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ITestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldBlockCondition(WorldCondition condition) implements BlockCondition, ITestWorldMetaCondition {

	public static final MapCodec<TestWorldBlockCondition> CODEC = ITestWorldMetaCondition.createCodec(TestWorldBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldBlockCondition> STREAM_CODEC = ITestWorldMetaCondition.createStreamCodec(TestWorldBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.TEST_WORLD;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}
