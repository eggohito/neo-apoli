package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.meta.bientity.SequenceBiEntityAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public abstract class BiEntityAction extends Action {

	public static final Codec<BiEntityAction> CODEC = Codec.recursive(BiEntityAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BiEntityActionTypes.CODEC.dispatch("type", BiEntityAction::getType, BiEntityActionType::mapCodec), codec.listOf().xmap(SequenceBiEntityAction::new, SequenceBiEntityAction::actions)));
	public static final PacketCodec<RegistryByteBuf, BiEntityAction> PACKET_CODEC = BiEntityActionTypes.PACKET_CODEC.dispatch(BiEntityAction::getType, BiEntityActionType::packetCodec);

	@Override
	public abstract BiEntityActionType<?> getType();

	@Override
	public ActionCategory<BiEntityAction> getCategory() {
		return ActionCategories.BIENTITY_ACTION;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.ACTOR, ContextParameters.TARGET);
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.BIENTITY_ACTION_TYPE, this.getType()) + "\"";
	}

}
