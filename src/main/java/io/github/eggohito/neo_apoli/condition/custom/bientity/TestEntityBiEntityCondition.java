package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestEntityMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public record TestEntityBiEntityCondition(EntityCondition condition, TypedContextKey<Entity> entity) implements BiEntityCondition, TestEntityMetaCondition {

	public static final MapCodec<TestEntityBiEntityCondition> CODEC = TestEntityMetaCondition.createCodec(TestEntityBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestEntityBiEntityCondition> STREAM_CODEC = TestEntityMetaCondition.createStreamCodec(TestEntityBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.TEST_ENTITY;
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return TestEntityMetaCondition.super.getRequiredParameters();
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}
