package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ITestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldBiEntityCondition(WorldCondition condition) implements BiEntityCondition, ITestWorldMetaCondition {

	public static final MapCodec<TestWorldBiEntityCondition> MAP_CODEC = ITestWorldMetaCondition.mapCodec(TestWorldBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldBiEntityCondition> STREAM_CODEC = ITestWorldMetaCondition.streamCodec(TestWorldBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.TEST_WORLD;
	}

}
