package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldEntityCondition(WorldCondition condition) implements EntityCondition, TestWorldMetaCondition {

	public static final MapCodec<TestWorldEntityCondition> MAP_CODEC = TestWorldMetaCondition.mapCodec(TestWorldEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldEntityCondition> STREAM_CODEC = TestWorldMetaCondition.streamCodec(TestWorldEntityCondition::new);

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.TEST_WORLD;
	}

}
