package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.custom.meta.ExecuteOnEntityMetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record ExecuteOnEntityAction(EntityAction action, TypedContextKey<Entity> entity) implements ExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityAction> CODEC = ExecuteOnEntityMetaAction.createCodec(ExecuteOnEntityAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteOnEntityAction> STREAM_CODEC = ExecuteOnEntityMetaAction.createStreamCodec(ExecuteOnEntityAction::new);

	@Override
	public ActionType<?> getType() {
		return MetaActionTypes.EXECUTE_ON_ENTITY;
	}

}
