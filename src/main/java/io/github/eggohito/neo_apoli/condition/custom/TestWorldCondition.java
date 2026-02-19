package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldCondition(WorldCondition condition) implements TestWorldMetaCondition {

	public static final MapCodec<TestWorldCondition> MAP_CODEC = TestWorldMetaCondition.mapCodec(TestWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldCondition> STREAM_CODEC = TestWorldMetaCondition.streamCodec(TestWorldCondition::new);

	@Override
	public ConditionType<?> getType() {
		return ConditionTypes.TEST_WORLD;
	}

}
