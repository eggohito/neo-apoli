package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.context.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public abstract class EntityCondition extends Condition<EntityConditionContext, EntityConditionType<?>> {

	public static final Codec<EntityCondition> BASE_CODEC = EntityConditionTypes.CODEC.dispatch(TYPE_KEY, EntityCondition::getType, EntityConditionType::mapCodec);
	public static final PacketCodec<RegistryByteBuf, EntityCondition> BASE_PACKET_CODEC = EntityConditionTypes.PACKET_CODEC.dispatch(EntityCondition::getType, EntityConditionType::packetCodec);

	public EntityCondition(boolean inverted) {
		super(inverted);
	}

	@Override
	public boolean test(EntityConditionContext context) {
		return context.entity() != null
			&& super.test(context);
	}

}
