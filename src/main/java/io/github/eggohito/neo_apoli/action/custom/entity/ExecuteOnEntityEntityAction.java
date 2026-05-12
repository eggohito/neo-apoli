package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ExecuteOnEntityMetaAction;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public record ExecuteOnEntityEntityAction(EntityAction action, Context.Parameter<Entity> entity) implements EntityAction, ExecuteOnEntityMetaAction {

	public static final MapCodec<ExecuteOnEntityEntityAction> MAP_CODEC = MapCodecUtil.lazy(ExecuteOnEntityEntityAction.class.getSimpleName(), () -> ExecuteOnEntityMetaAction.mapCodec(ExecuteOnEntityEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ExecuteOnEntityEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(ExecuteOnEntityEntityAction.class.getSimpleName(), () -> ExecuteOnEntityMetaAction.streamCodec(ExecuteOnEntityEntityAction::new));

	@Override
	public EntityAction.Type<?> getType() {
		return NeoApoliEntityActionTypes.EXECUTE_ON_ENTITY;
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return ExecuteOnEntityMetaAction.super.getRequiredParameters();
	}

}
