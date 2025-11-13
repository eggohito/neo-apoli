package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ExtinguishEntityAction() implements EntityAction {

	public static final MapCodec<ExtinguishEntityAction> CODEC = MapCodec.unit(ExtinguishEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExtinguishEntityAction> PACKET_CODEC = PacketCodecUtil.unit(ExtinguishEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXTINGUISH;
	}

	@Override
	public void execute(Context context) {
		context.optional(ContextParameters.THIS_ENTITY).ifPresent(Entity::extinguishWithSound);
	}

}
