package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IConditionalMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalBlockAction(BlockCondition condition, BlockAction ifAction, Optional<BlockAction> elseAction) implements BlockAction, IConditionalMetaAction<BlockCondition, BlockAction> {

	public static final MapCodec<ConditionalBlockAction> MAP_CODEC = MapCodecUtil.lazy(ConditionalBlockAction.class.getSimpleName(), () -> IConditionalMetaAction.mapCodec(BlockCondition.CODEC, BlockAction.CODEC, ConditionalBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalBlockAction.class.getSimpleName(), () -> IConditionalMetaAction.streamCodec(BlockCondition.STREAM_CODEC, BlockAction.STREAM_CODEC, ConditionalBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.CONDITIONAL;
	}

}
