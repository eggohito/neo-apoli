package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class DismountEntityAction extends EntityAction {

	public static final MapCodec<DismountEntityAction> CODEC = MapCodec.unit(DismountEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, DismountEntityAction> PACKET_CODEC = PacketCodec.unit(new DismountEntityAction());

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.DISMOUNT;
	}

	@Override
	protected void impl(Context context) {
		context.required(ContextParameters.ENTITY).stopRiding();
	}

}
