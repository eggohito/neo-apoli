package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ITestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldKeyCondition(WorldCondition condition) implements KeyCondition, ITestWorldMetaCondition {

	public static final MapCodec<TestWorldKeyCondition> CODEC = ITestWorldMetaCondition.createCodec(TestWorldKeyCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldKeyCondition> STREAM_CODEC = ITestWorldMetaCondition.createStreamCodec(TestWorldKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.TEST_WORLD;
	}

	@Override
	public String asDisplayString() {
		return KeyCondition.super.asDisplayString();
	}

}
