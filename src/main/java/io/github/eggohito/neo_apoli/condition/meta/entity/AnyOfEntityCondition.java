package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.context.entity.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.meta.MultiMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AnyOfEntityCondition(List<EntityCondition> conditions) implements EntityCondition, AnyOfMetaCondition<EntityConditionContext, EntityCondition, EntityConditionType<?>> {

	public static final MapCodec<AnyOfEntityCondition> CODEC = MultiMetaCondition.createCodec(EntityCondition.CODEC, AnyOfEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, AnyOfEntityCondition> PACKET_CODEC = MultiMetaCondition.createPacketCodec(EntityCondition.PACKET_CODEC, AnyOfEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ANY_OF;
	}

}
