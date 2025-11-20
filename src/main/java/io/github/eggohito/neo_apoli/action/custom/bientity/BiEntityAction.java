package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface BiEntityAction extends Action {

	Codec<BiEntityAction> CODEC = Codec.recursive(BiEntityAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BiEntityActionType.CODEC.dispatch(BiEntityAction::getType, BiEntityActionType::mapCodec), codec.listOf().xmap(SequenceBiEntityAction::new, SequenceBiEntityAction::actions), NothingBiEntityAction.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, BiEntityAction> PACKET_CODEC = BiEntityActionType.PACKET_CODEC.dispatch(BiEntityAction::getType, BiEntityActionType::packetCodec);

	@Override
	BiEntityActionType<?> getType();

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return NeoApoliContextTypes.BIENTITY.getAllowed();
	}

	@Override
	default String asDisplayString() {
		return "Bi-entity action with type \"" + RegistryUtil.getId(NeoApoliRegistries.ACTION_TYPE, this.getType()) + "\"";
	}

}
