package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.entity.EntityAction;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionType;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record ExecuteOnEntityMetaAction(EntityAction action, TypedContextKey<Entity> entity) implements IExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityMetaAction> CODEC = IExecuteOnEntityMetaAction.createCodec(ExecuteOnEntityMetaAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteOnEntityMetaAction> STREAM_CODEC = IExecuteOnEntityMetaAction.createStreamCodec(ExecuteOnEntityMetaAction::new);

	@Override
	public MetaActionType<?> getType() {
		return MetaActionTypes.EXECUTE_ON_ENTITY;
	}

}
