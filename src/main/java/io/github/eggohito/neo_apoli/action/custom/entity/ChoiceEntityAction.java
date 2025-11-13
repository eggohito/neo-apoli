package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record ChoiceEntityAction(List<Case<EntityCondition, EntityAction>> cases, EntityAction defaultAction) implements EntityAction, ChoiceMetaAction<EntityCondition, EntityAction> {

	public static final MapCodec<ChoiceEntityAction> CODEC = MapCodecUtil.lazy(ChoiceEntityAction.class.getSimpleName(), () -> ChoiceMetaAction.codec(EntityCondition.CODEC, EntityAction.CODEC, ChoiceEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, ChoiceEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(ChoiceEntityAction.class.getSimpleName(), () -> ChoiceMetaAction.packetCodec(EntityCondition.PACKET_CODEC, EntityAction.PACKET_CODEC, ChoiceEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.CHOICE;
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}
