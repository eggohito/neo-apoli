package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldBiEntityCondition(WorldCondition condition) implements BiEntityCondition, TestWorldMetaCondition {

	public static final MapCodec<TestWorldBiEntityCondition> MAP_CODEC = TestWorldMetaCondition.mapCodec(TestWorldBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldBiEntityCondition> STREAM_CODEC = TestWorldMetaCondition.streamCodec(TestWorldBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.TEST_WORLD;
	}

}
