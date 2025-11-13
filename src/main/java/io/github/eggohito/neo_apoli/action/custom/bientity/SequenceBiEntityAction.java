package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record SequenceBiEntityAction(List<BiEntityAction> actions) implements BiEntityAction, SequenceMetaAction<BiEntityAction> {

	public static final MapCodec<SequenceBiEntityAction> CODEC = MapCodecUtil.lazy(SequenceBiEntityAction.class.getSimpleName(), () -> SequenceMetaAction.codec(BiEntityAction.CODEC, SequenceBiEntityAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceBiEntityAction> PACKET_CODEC = PacketCodecUtil.lazy(SequenceBiEntityAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(BiEntityAction.PACKET_CODEC, SequenceBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SEQUENCE;
	}

	@Override
	public String asDisplayString() {
		return BiEntityAction.super.asDisplayString();
	}

}
