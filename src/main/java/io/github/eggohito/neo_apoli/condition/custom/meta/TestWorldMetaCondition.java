package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldMetaCondition(WorldCondition condition) implements ITestWorldMetaCondition {

	public static final MapCodec<TestWorldMetaCondition> CODEC = ITestWorldMetaCondition.createCodec(TestWorldMetaCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldMetaCondition> STREAM_CODEC = ITestWorldMetaCondition.createStreamCodec(TestWorldMetaCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.TEST_WORLD;
	}

}
