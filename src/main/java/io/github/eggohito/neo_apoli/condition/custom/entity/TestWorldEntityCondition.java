package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldEntityCondition(WorldCondition condition) implements EntityCondition, TestWorldMetaCondition {

	public static final MapCodec<TestWorldEntityCondition> CODEC = TestWorldMetaCondition.createCodec(TestWorldEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldEntityCondition> STREAM_CODEC = TestWorldMetaCondition.createStreamCodec(TestWorldEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.TEST_WORLD;
	}

	@Override
	public String asDisplayString() {
		return EntityCondition.super.asDisplayString();
	}

}
