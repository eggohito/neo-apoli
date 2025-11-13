package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.custom.meta.ExecuteOnEntityMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record ExecuteOnEntityBiEntityAction(EntityAction action, EntityTarget entity) implements BiEntityAction, ExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityBiEntityAction> CODEC = ExecuteOnEntityMetaAction.codec(ExecuteOnEntityBiEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExecuteOnEntityBiEntityAction> PACKET_CODEC = ExecuteOnEntityMetaAction.packetCodec(ExecuteOnEntityBiEntityAction::new);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.EXECUTE_ON_ENTITY;
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return ExecuteOnEntityMetaAction.super.getRequiredParameters();
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}
