package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record IfElseEntityAction(EntityCondition condition, EntityAction ifAction, Optional<EntityAction> elseAction) implements EntityAction, IfElseMetaAction<EntityAction, EntityCondition, EntityActionType<?>, EntityConditionType<?>> {

	public static final MapCodec<IfElseEntityAction> CODEC = NeoApoliMapCodecs.lazy(IfElseEntityAction.class.getSimpleName(), () -> IfElseMetaAction.codec(EntityCondition.CODEC, EntityAction.CODEC, IfElseEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(IfElseEntityAction.class.getSimpleName(), () -> IfElseMetaAction.packetCodec(EntityCondition.PACKET_CODEC, EntityAction.PACKET_CODEC, IfElseEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.IF_ELSE;
	}

}
