package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record ExecuteOnEntityMetaAction(EntityAction action, ContextParameter<Entity> entity) implements IExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityMetaAction> MAP_CODEC = IExecuteOnEntityMetaAction.mapCodec(ExecuteOnEntityMetaAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteOnEntityMetaAction> STREAM_CODEC = IExecuteOnEntityMetaAction.streamCodec(ExecuteOnEntityMetaAction::new);

	@Override
	public MetaActionType<?> getType() {
		return MetaActionTypes.EXECUTE_ON_ENTITY;
	}

}
