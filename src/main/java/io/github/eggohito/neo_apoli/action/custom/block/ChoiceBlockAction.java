package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceBlockAction(List<Case<BlockCondition, BlockAction>> cases, BlockAction defaultAction) implements BlockAction, IChoiceMetaAction<BlockCondition, BlockAction> {

	public static final MapCodec<ChoiceBlockAction> MAP_CODEC = MapCodecUtil.lazy(ChoiceBlockAction.class.getSimpleName(), () -> IChoiceMetaAction.mapCodec(BlockCondition.CODEC, BlockAction.CODEC, ChoiceBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceBlockAction.class.getSimpleName(), () -> IChoiceMetaAction.streamCodec(BlockCondition.STREAM_CODEC, BlockAction.STREAM_CODEC, ChoiceBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.CHOICE;
	}

}
