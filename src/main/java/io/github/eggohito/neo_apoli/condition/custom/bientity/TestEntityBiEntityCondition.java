package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestEntityMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record TestEntityBiEntityCondition(EntityCondition condition, EntityTarget entity) implements BiEntityCondition, TestEntityMetaCondition {

	public static final MapCodec<TestEntityBiEntityCondition> CODEC = TestEntityMetaCondition.codec(TestEntityBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, TestEntityBiEntityCondition> PACKET_CODEC = TestEntityMetaCondition.packetCodec(TestEntityBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.TEST_ENTITY;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}
