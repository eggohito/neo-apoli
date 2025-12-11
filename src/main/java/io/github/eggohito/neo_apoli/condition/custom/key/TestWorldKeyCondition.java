package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldKeyCondition(WorldCondition condition) implements KeyCondition, TestWorldMetaCondition {

	public static final MapCodec<TestWorldKeyCondition> CODEC = TestWorldMetaCondition.createCodec(TestWorldKeyCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldKeyCondition> STREAM_CODEC = TestWorldMetaCondition.createStreamCodec(TestWorldKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.TEST_WORLD;
	}

	@Override
	public String asDisplayString() {
		return KeyCondition.super.asDisplayString();
	}

}
