package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestEntityMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record TestEntityCondition(EntityCondition condition, EntityTarget entity) implements TestEntityMetaCondition {

	public static final MapCodec<TestEntityCondition> CODEC = MapCodecUtil.lazy(TestEntityCondition.class.getSimpleName(), () -> TestEntityMetaCondition.codec(TestEntityCondition::new));
	public static final PacketCodec<RegistryByteBuf, TestEntityCondition> PACKET_CODEC = PacketCodecUtil.lazy(TestEntityCondition.class.getSimpleName(), () -> TestEntityMetaCondition.packetCodec(TestEntityCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.TEST_ENTITY;
	}

}
