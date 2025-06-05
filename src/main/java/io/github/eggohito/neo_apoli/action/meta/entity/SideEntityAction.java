package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.SideMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record SideEntityAction(EntityAction action, Side side) implements EntityAction, SideMetaAction<EntityAction, EntityActionType<?>> {

	public static final MapCodec<SideEntityAction> CODEC = NeoApoliMapCodecs.lazy(SideEntityAction.class.getSimpleName(), () -> SideMetaAction.codec(EntityAction.CODEC, SideEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, SideEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(SideEntityAction.class.getSimpleName(), () -> SideMetaAction.packetCodec(EntityAction.PACKET_CODEC, SideEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SIDE;
	}

}
