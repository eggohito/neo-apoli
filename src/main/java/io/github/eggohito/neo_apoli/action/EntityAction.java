package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.context.entity.EntityActionContext;
import io.github.eggohito.neo_apoli.action.meta.entity.SequenceEntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface EntityAction extends Action<EntityActionContext, EntityActionType<?>> {

	Codec<EntityAction> CODEC = Codec.recursive("EntityAction", codec -> Codec.withAlternative(EntityActionTypes.CODEC.dispatch(TYPE_KEY, EntityAction::getType, EntityActionType::mapCodec), codec.listOf().xmap(SequenceEntityAction::new, SequenceEntityAction::actions)));
	PacketCodec<RegistryByteBuf, EntityAction> PACKET_CODEC = EntityActionTypes.PACKET_CODEC.dispatch(EntityAction::getType, EntityActionType::packetCodec);

}
