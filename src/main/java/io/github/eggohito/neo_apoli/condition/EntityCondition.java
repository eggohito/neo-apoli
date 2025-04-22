package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.context.entity.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface EntityCondition extends Condition<EntityConditionContext, EntityConditionType<?>> {

	Codec<EntityCondition> CODEC = EntityConditionTypes.CODEC.dispatch(TYPE_KEY, EntityCondition::getType, EntityConditionType::mapCodec);
	PacketCodec<RegistryByteBuf, EntityCondition> PACKET_CODEC = EntityConditionTypes.PACKET_CODEC.dispatch(EntityCondition::getType, EntityConditionType::packetCodec);

	@Override
	default String asDisplayString() {
		return "Entity condition type \"" + RegistryUtil.getId(NeoApoliRegistries.ENTITY_CONDITION_TYPE, this.getType()) + "\"";
	}

}
