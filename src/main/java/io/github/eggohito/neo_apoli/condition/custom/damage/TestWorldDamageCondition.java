package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestWorldMetaCondition;
import io.github.eggohito.neo_apoli.condition.custom.world.WorldCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestWorldDamageCondition(WorldCondition condition) implements DamageCondition, TestWorldMetaCondition {

	public static final MapCodec<TestWorldDamageCondition> CODEC = TestWorldMetaCondition.createCodec(TestWorldDamageCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestWorldDamageCondition> STREAM_CODEC = TestWorldMetaCondition.createStreamCodec(TestWorldDamageCondition::new);

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.TEST_WORLD;
	}

	@Override
	public String asDisplayString() {
		return DamageCondition.super.asDisplayString();
	}

}
