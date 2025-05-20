package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.meta.bientity.ReferenceBiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.bientity.SequenceBiEntityAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface BiEntityAction extends Action<BiEntityActionType<?>> {

	Codec<BiEntityAction> CODEC = Codec.recursive(BiEntityAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BiEntityActionTypes.CODEC.dispatch(TYPE_KEY, BiEntityAction::getType, BiEntityActionType::mapCodec), Identifier.CODEC.xmap(ReferenceBiEntityAction::new, ReferenceBiEntityAction::value), codec.listOf().xmap(SequenceBiEntityAction::new, SequenceBiEntityAction::actions)));
	PacketCodec<RegistryByteBuf, BiEntityAction> PACKET_CODEC = BiEntityActionTypes.PACKET_CODEC.dispatch(BiEntityAction::getType, BiEntityActionType::packetCodec);

	@Override
	default ActionCategory<BiEntityAction> getCategory() {
		return ActionCategories.BIENTITY_ACTION;
	}

	@Override
	default String asDisplayString() {
		return ActionManager.getIdAsResult(this)
			.result()
			.map(id -> "Bi-entity action with ID \"" + id + "\"")
			.orElseGet(() -> "Bi-entity action with type \"" + RegistryUtil.getId(NeoApoliRegistries.BIENTITY_ACTION_TYPE, this.getType()) + "\"");
	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.ACTOR, ContextParameters.TARGET);
	}

}
