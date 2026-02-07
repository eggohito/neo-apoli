package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ISequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SequenceBlockAction(List<BlockAction> actions) implements BlockAction, ISequenceMetaAction<BlockAction> {

	public static final MapCodec<SequenceBlockAction> MAP_CODEC = MapCodecUtil.lazy(SequenceBlockAction.class.getSimpleName(), () -> ISequenceMetaAction.mapCodec(BlockAction.CODEC, SequenceBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SequenceBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(SequenceBlockAction.class.getSimpleName(), () -> ISequenceMetaAction.streamCodec(BlockAction.STREAM_CODEC, SequenceBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.SEQUENCE;
	}

}
