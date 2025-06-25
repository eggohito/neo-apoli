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

@EqualsAndHashCode(callSuper = false)
@Data
public final class ExtinguishEntityAction extends EntityAction {

	public static final MapCodec<ExtinguishEntityAction> CODEC = MapCodec.unit(ExtinguishEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExtinguishEntityAction> PACKET_CODEC = PacketCodec.unit(new ExtinguishEntityAction());

	public ExtinguishEntityAction() {

	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXTINGUISH;
	}

	@Override
	protected void impl(Context context) {
		context.required(ContextParameters.THIS_ENTITY).extinguishWithSound();
	}

}
