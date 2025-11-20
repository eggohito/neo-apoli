package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record DismountEntityAction() implements EntityAction {

	public static final MapCodec<DismountEntityAction> CODEC = MapCodec.unit(DismountEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, DismountEntityAction> PACKET_CODEC = PacketCodecUtil.unit(DismountEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.DISMOUNT;
	}

	@Override
	public void execute(Context context) {
		context.optional(NeoApoliContextParameters.THIS_ENTITY).ifPresent(Entity::stopRiding);
	}

}
