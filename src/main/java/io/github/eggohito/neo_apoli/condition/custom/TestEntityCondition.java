package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestEntityMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TestEntityCondition(EntityCondition condition, EntityTarget entity) implements TestEntityMetaCondition {

	public static final MapCodec<TestEntityCondition> CODEC = MapCodecUtil.lazy(TestEntityCondition.class.getSimpleName(), () -> TestEntityMetaCondition.createCodec(TestEntityCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, TestEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(TestEntityCondition.class.getSimpleName(), () -> TestEntityMetaCondition.createStreamCodec(TestEntityCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.TEST_ENTITY;
	}

}
