package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.custom.meta.ExecuteOnEntityMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ExecuteOnEntityAction(EntityAction action, EntityTarget entity) implements ExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityAction> CODEC = ExecuteOnEntityMetaAction.codec(ExecuteOnEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExecuteOnEntityAction> PACKET_CODEC = ExecuteOnEntityMetaAction.packetCodec(ExecuteOnEntityAction::new);

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.EXECUTE_ON_ENTITY;
	}

}
