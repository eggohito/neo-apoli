package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.context.entity.EntityActionContext;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ExtinguishEntityAction() implements EntityAction {

	public static final MapCodec<ExtinguishEntityAction> CODEC = MapCodec.unit(ExtinguishEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExtinguishEntityAction> PACKET_CODEC = PacketCodec.unit(new ExtinguishEntityAction());

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXTINGUISH;
	}

	@Override
	public void accept(EntityActionContext context) {
		context.entity().ifPresent(Entity::extinguishWithSound);
	}

}
