package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.context.entity.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface EntityCondition extends Condition<EntityConditionContext, EntityConditionType<?>> {

	Codec<EntityCondition> BASE_CODEC = EntityConditionTypes.CODEC.dispatch(TYPE_KEY, EntityCondition::getType, EntityConditionType::mapCodec);
	PacketCodec<RegistryByteBuf, EntityCondition> BASE_PACKET_CODEC = EntityConditionTypes.PACKET_CODEC.dispatch(EntityCondition::getType, EntityConditionType::packetCodec);

}
