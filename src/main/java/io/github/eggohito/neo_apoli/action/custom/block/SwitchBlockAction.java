package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SwitchMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SwitchBlockAction(List<Case<BlockCondition, BlockAction>> cases, BlockAction defaultAction) implements BlockAction, SwitchMetaAction<BlockCondition, BlockAction> {

	public static final MapCodec<SwitchBlockAction> MAP_CODEC = MapCodecUtil.lazy(SwitchBlockAction.class.getSimpleName(), () -> SwitchMetaAction.mapCodec(BlockCondition.CODEC, BlockAction.CODEC, SwitchBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(SwitchBlockAction.class.getSimpleName(), () -> SwitchMetaAction.streamCodec(BlockCondition.STREAM_CODEC, BlockAction.STREAM_CODEC, SwitchBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.SWITCH;
	}

}
