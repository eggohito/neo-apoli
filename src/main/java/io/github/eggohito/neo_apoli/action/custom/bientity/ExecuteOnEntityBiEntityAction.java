package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.custom.meta.IExecuteOnEntityMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public record ExecuteOnEntityBiEntityAction(EntityAction action, TypedContextKey<Entity> entity) implements BiEntityAction, IExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityBiEntityAction> CODEC = IExecuteOnEntityMetaAction.createCodec(ExecuteOnEntityBiEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteOnEntityBiEntityAction> STREAM_CODEC = IExecuteOnEntityMetaAction.createStreamCodec(ExecuteOnEntityBiEntityAction::new);

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.EXECUTE_ON_ENTITY;
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return IExecuteOnEntityMetaAction.super.getRequiredParameters();
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}
