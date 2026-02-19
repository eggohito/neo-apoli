package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestEntityMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public record TestEntityBiEntityCondition(EntityCondition condition, ContextParameter<Entity> entity) implements BiEntityCondition, TestEntityMetaCondition {

	public static final MapCodec<TestEntityBiEntityCondition> MAP_CODEC = TestEntityMetaCondition.mapCodec(TestEntityBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, TestEntityBiEntityCondition> STREAM_CODEC = TestEntityMetaCondition.streamCodec(TestEntityBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.TEST_ENTITY;
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return TestEntityMetaCondition.super.getRequiredParameters();
	}

}
