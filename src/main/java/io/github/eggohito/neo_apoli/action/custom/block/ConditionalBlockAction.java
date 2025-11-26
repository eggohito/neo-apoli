package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ConditionalMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalBlockAction(BlockCondition condition, BlockAction ifAction, Optional<BlockAction> elseAction) implements BlockAction, ConditionalMetaAction<BlockCondition, BlockAction> {

	public static final MapCodec<ConditionalBlockAction> CODEC = MapCodecUtil.lazy(ConditionalBlockAction.class.getSimpleName(), () -> ConditionalMetaAction.createCodec(BlockCondition.CODEC, BlockAction.CODEC, ConditionalBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalBlockAction.class.getSimpleName(), () -> ConditionalMetaAction.createStreamCodec(BlockCondition.STREAM_CODEC, BlockAction.STREAM_CODEC, ConditionalBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.CONDITIONAL;
	}

	@Override
	public void execute(Context context) {
		ConditionalMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		ConditionalMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return BlockAction.super.asDisplayString();
	}

}
