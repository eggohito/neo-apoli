package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface EntityAction extends Action {

	Codec<EntityAction> CODEC = Codec.recursive(EntityAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(EntityActionType.CODEC.dispatch(EntityAction::getType, EntityActionType::mapCodec), codec.listOf().xmap(SequenceEntityAction::new, SequenceEntityAction::actions), NothingEntityAction.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, EntityAction> STREAM_CODEC = EntityActionType.STREAM_CODEC.dispatch(EntityAction::getType, EntityActionType::streamCodec);

	@Override
	EntityActionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.THIS_ENTITY);
	}

	@Override
	default String asDisplayString() {
		return "Entity action with type \"" + RegistryUtil.getId(NeoApoliRegistries.ACTION_TYPE, this.getType()) + "\"";
	}

}
