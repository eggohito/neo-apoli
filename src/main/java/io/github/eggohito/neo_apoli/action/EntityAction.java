package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.meta.entity.SequenceEntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface EntityAction extends Action<EntityActionType<?>> {

	Codec<EntityAction> CODEC = Codec.recursive("EntityAction", codec -> new ValueSuppliedElementCodec<>(Codec.withAlternative(EntityActionTypes.CODEC.dispatch(TYPE_KEY, EntityAction::getType, EntityActionType::mapCodec), codec.listOf().xmap(SequenceEntityAction::new, SequenceEntityAction::actions)), true, id -> ActionManager.getAsResult(ActionCategories.ENTITY_ACTION, id), ActionManager::getIdAsResult));
	PacketCodec<RegistryByteBuf, EntityAction> PACKET_CODEC = EntityActionTypes.PACKET_CODEC.dispatch(EntityAction::getType, EntityActionType::packetCodec);

	@Override
	default ActionCategory<EntityAction> getCategory() {
		return ActionCategories.ENTITY_ACTION;
	}

	@Override
	default String asDisplayString() {
		return ActionManager.getIdAsResult(this)
			.result()
			.map(id -> "Entity action with ID \"" + id + "\"")
			.orElseGet(() -> "Entity action with type \"" + RegistryUtil.getId(NeoApoliRegistries.ENTITY_ACTION_TYPE, this.getType()) + "\"");
	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.CURRENT_ENTITY, ContextParameters.POSITION);
	}

}
