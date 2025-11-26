package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ExecuteOnEntityMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public record ExecuteOnEntityEntityAction(EntityAction action, TypedContextKey<Entity> entity) implements EntityAction, ExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityEntityAction> CODEC = MapCodecUtil.lazy(ExecuteOnEntityEntityAction.class.getSimpleName(), () -> ExecuteOnEntityMetaAction.createCodec(ExecuteOnEntityEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteOnEntityEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(ExecuteOnEntityEntityAction.class.getSimpleName(), () -> ExecuteOnEntityMetaAction.createStreamCodec(ExecuteOnEntityEntityAction::new));

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXECUTE_ON_ENTITY;
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return ExecuteOnEntityMetaAction.super.getRequiredParameters();
	}

	@Override
	public String asDisplayString() {
		return EntityAction.super.asDisplayString();
	}

}
