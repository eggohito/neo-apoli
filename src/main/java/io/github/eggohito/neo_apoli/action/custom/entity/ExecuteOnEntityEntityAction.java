package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IExecuteOnEntityMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public record ExecuteOnEntityEntityAction(EntityAction action, ContextParameter<Entity> entity) implements EntityAction, IExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityEntityAction> MAP_CODEC = MapCodecUtil.lazy(ExecuteOnEntityEntityAction.class.getSimpleName(), () -> IExecuteOnEntityMetaAction.mapCodec(ExecuteOnEntityEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteOnEntityEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(ExecuteOnEntityEntityAction.class.getSimpleName(), () -> IExecuteOnEntityMetaAction.streamCodec(ExecuteOnEntityEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXECUTE_ON_ENTITY;
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return IExecuteOnEntityMetaAction.super.getRequiredParameters();
	}

}
