package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.context.entity.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantEntityCondition(boolean value) implements EntityCondition, ConstantMetaCondition<EntityConditionContext, EntityConditionType<?>> {

	public static final MapCodec<ConstantEntityCondition> CODEC = ConstantMetaCondition.createCodec(ConstantEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, ConstantEntityCondition> PACKET_CODEC = ConstantMetaCondition.createPacketCodec(ConstantEntityCondition::new).cast();

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.CONSTANT;
	}

}
