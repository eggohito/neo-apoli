package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.custom.meta.ExecuteOnEntityMetaAction;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBiEntityActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public record ExecuteOnEntityBiEntityAction(EntityAction action, Context.Parameter<Entity> entity) implements BiEntityAction, ExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityBiEntityAction> MAP_CODEC = ExecuteOnEntityMetaAction.mapCodec(ExecuteOnEntityBiEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteOnEntityBiEntityAction> STREAM_CODEC = ExecuteOnEntityMetaAction.streamCodec(ExecuteOnEntityBiEntityAction::new);

	@Override
	public BiEntityAction.Type<?> getType() {
		return NeoApoliBiEntityActionTypes.EXECUTE_ON_ENTITY;
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return ExecuteOnEntityMetaAction.super.getRequiredParameters();
	}

}
