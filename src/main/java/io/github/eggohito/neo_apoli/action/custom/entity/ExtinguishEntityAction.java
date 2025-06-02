package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
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
	public void execute(Context context) {
		context.required(ContextParameters.THIS_ENTITY).extinguishWithSound();
	}

}
