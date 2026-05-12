package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldCondition(WorldCondition condition) implements TestWorldMetaCondition {

	public static final MapCodec<TestWorldCondition> MAP_CODEC = TestWorldMetaCondition.mapCodec(TestWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldCondition> STREAM_CODEC = TestWorldMetaCondition.streamCodec(TestWorldCondition::new);

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.TEST_WORLD;
	}

}
