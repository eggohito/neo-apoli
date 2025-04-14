package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.context.entity.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedEntityCondition(EntityCondition condition) implements EntityCondition, InvertedMetaCondition<EntityConditionContext, EntityConditionType<?>, EntityCondition> {

	public static final MapCodec<InvertedEntityCondition> CODEC = InvertedMetaCondition.createCodec(EntityCondition.CODEC, InvertedEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, InvertedEntityCondition> PACKET_CODEC = InvertedMetaCondition.createPacketCodec(EntityCondition.PACKET_CODEC, InvertedEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.INVERTED;
	}

}
