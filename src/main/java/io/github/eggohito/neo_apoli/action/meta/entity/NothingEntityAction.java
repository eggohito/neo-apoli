package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record NothingEntityAction() implements EntityAction, NothingMetaAction<EntityActionType<?>> {

	public static final MapCodec<NothingEntityAction> CODEC = MapCodec.unit(NothingEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, NothingEntityAction> PACKET_CODEC = PacketCodec.unit(new NothingEntityAction());

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.NOTHING;
	}

}
