package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.SideMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record SideEntityAction(EntityAction action, Side side) implements EntityAction, SideMetaAction<EntityAction, EntityActionType<?>> {

	public static final MapCodec<SideEntityAction> CODEC = SideMetaAction.createCodec(EntityAction.CODEC, SideEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, SideEntityAction> PACKET_CODEC = SideMetaAction.createPacketCodec(EntityAction.PACKET_CODEC, SideEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SIDE;
	}

}
