package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.context.entity.EntityActionContext;
import io.github.eggohito.neo_apoli.action.meta.IfElseListMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.context.entity.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record IfElseListEntityAction(List<Entry<EntityCondition, EntityAction>> entries) implements EntityAction, IfElseListMetaAction<EntityActionContext, EntityConditionContext, EntityAction, EntityCondition, EntityActionType<?>, EntityConditionType<?>> {

	public static final MapCodec<IfElseListEntityAction> CODEC = IfElseListMetaAction.createCodec(EntityCondition.CODEC, EntityAction.CODEC, IfElseListEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, IfElseListEntityAction> PACKET_CODEC = IfElseListMetaAction.createPacketCodec(EntityCondition.PACKET_CODEC, EntityAction.PACKET_CODEC, IfElseListEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.IF_ELSE_LIST;
	}

}
