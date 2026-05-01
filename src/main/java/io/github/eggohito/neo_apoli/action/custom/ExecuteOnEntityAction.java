package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.custom.meta.ExecuteOnEntityMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record ExecuteOnEntityAction(EntityAction action, Context.Parameter<Entity> entity) implements ExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityAction> MAP_CODEC = ExecuteOnEntityMetaAction.mapCodec(ExecuteOnEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteOnEntityAction> STREAM_CODEC = ExecuteOnEntityMetaAction.streamCodec(ExecuteOnEntityAction::new);

	@Override
	public ActionType<?> getType() {
		return ActionTypes.EXECUTE_ON_ENTITY;
	}

}
