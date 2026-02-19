package io.github.eggohito.neo_apoli.action.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionType;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SequenceBiEntityAction(List<BiEntityAction> actions) implements BiEntityAction, SequenceMetaAction<BiEntityAction> {

	public static final MapCodec<SequenceBiEntityAction> MAP_CODEC = MapCodecUtil.lazy(SequenceBiEntityAction.class.getSimpleName(), () -> SequenceMetaAction.mapCodec(BiEntityAction.CODEC, SequenceBiEntityAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SequenceBiEntityAction> STREAM_CODEC = StreamCodecUtil.lazy(SequenceBiEntityAction.class.getSimpleName(), () -> SequenceMetaAction.streamCodec(BiEntityAction.STREAM_CODEC, SequenceBiEntityAction::new));

	@Override
	public BiEntityActionType<?> getType() {
		return BiEntityActionTypes.SEQUENCE;
	}

}
