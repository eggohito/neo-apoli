package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ITestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldEntityCondition(WorldCondition condition) implements EntityCondition, ITestWorldMetaCondition {

	public static final MapCodec<TestWorldEntityCondition> CODEC = ITestWorldMetaCondition.createCodec(TestWorldEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldEntityCondition> STREAM_CODEC = ITestWorldMetaCondition.createStreamCodec(TestWorldEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.TEST_WORLD;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}
