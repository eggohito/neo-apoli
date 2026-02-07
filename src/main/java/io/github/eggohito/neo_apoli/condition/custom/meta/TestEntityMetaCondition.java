package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record TestEntityMetaCondition(EntityCondition condition, ContextParameter<Entity> entity) implements ITestEntityMetaCondition {

	public static final MapCodec<TestEntityMetaCondition> MAP_CODEC = MapCodecUtil.lazy(TestEntityMetaCondition.class.getSimpleName(), () -> ITestEntityMetaCondition.mapCodec(TestEntityMetaCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, TestEntityMetaCondition> STREAM_CODEC = StreamCodecUtil.lazy(TestEntityMetaCondition.class.getSimpleName(), () -> ITestEntityMetaCondition.streamCodec(TestEntityMetaCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.TEST_ENTITY;
	}

}
