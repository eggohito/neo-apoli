package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBlockConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldBlockCondition(WorldCondition condition) implements BlockCondition, TestWorldMetaCondition {

	public static final MapCodec<TestWorldBlockCondition> MAP_CODEC = TestWorldMetaCondition.mapCodec(TestWorldBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldBlockCondition> STREAM_CODEC = TestWorldMetaCondition.streamCodec(TestWorldBlockCondition::new);

	@Override
	public BlockCondition.Type<?> getType() {
		return NeoApoliBlockConditionTypes.TEST_WORLD;
	}

}
