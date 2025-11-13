package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ExecuteOnEntityMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record ExecuteOnEntityEntityAction(EntityAction action, EntityTarget entity) implements EntityAction, ExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityEntityAction> CODEC = MapCodecUtil.lazy(ExecuteOnEntityEntityAction.class.getSimpleName(), () -> ExecuteOnEntityMetaAction.codec(ExecuteOnEntityEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, ExecuteOnEntityEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(ExecuteOnEntityEntityAction.class.getSimpleName(), () -> ExecuteOnEntityMetaAction.packetCodec(ExecuteOnEntityEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXECUTE_ON_ENTITY;
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return ExecuteOnEntityMetaAction.super.getRequiredParameters();
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}
