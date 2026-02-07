package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface BiEntityAction extends Action {

	Codec<BiEntityAction> CODEC = Codec.recursive(BiEntityAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BiEntityActionType.CODEC.dispatch(BiEntityAction::getType, BiEntityActionType::mapCodec), codec.listOf().xmap(SequenceBiEntityAction::new, SequenceBiEntityAction::actions)));

	StreamCodec<RegistryFriendlyByteBuf, BiEntityAction> STREAM_CODEC = BiEntityActionType.STREAM_CODEC.dispatch(BiEntityAction::getType, BiEntityActionType::streamCodec);

	@Override
	BiEntityActionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.ACTOR_ENTITY, NeoApoliContextParams.TARGET_ENTITY);
	}

}
