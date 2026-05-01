package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestEntityMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record TestEntityCondition(EntityCondition condition, Context.Parameter<Entity> entity) implements TestEntityMetaCondition {

	public static final MapCodec<TestEntityCondition> MAP_CODEC = MapCodecUtil.lazy(TestEntityCondition.class.getSimpleName(), () -> TestEntityMetaCondition.mapCodec(TestEntityCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, TestEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(TestEntityCondition.class.getSimpleName(), () -> TestEntityMetaCondition.streamCodec(TestEntityCondition::new));

	@Override
	public ConditionType<?> getType() {
		return ConditionTypes.TEST_ENTITY;
	}

}
