package io.github.eggohito.neo_apoli.action.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record SequenceBiEntityAction(List<BiEntityAction> actions) implements BiEntityAction, SequenceMetaAction<BiEntityAction, BiEntityActionType<?>> {

	public static final MapCodec<SequenceBiEntityAction> CODEC = NeoApoliMapCodecs.lazy(SequenceBiEntityAction.class.getSimpleName(), () -> SequenceMetaAction.codec(BiEntityAction.CODEC, SequenceBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceBiEntityAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(SequenceBiEntityAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, SequenceBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SEQUENCE;
	}

}
