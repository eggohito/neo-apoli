package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.ExecuteCommandMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ExecuteCommandEntityAction(StringProvider command) implements EntityAction, ExecuteCommandMetaAction<EntityActionType<?>> {

	public static final MapCodec<ExecuteCommandEntityAction> CODEC = ExecuteCommandMetaAction.codec(ExecuteCommandEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExecuteCommandEntityAction> PACKET_CODEC = ExecuteCommandMetaAction.packetCodec(ExecuteCommandEntityAction::new);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXECUTE_COMMAND;
	}

}
