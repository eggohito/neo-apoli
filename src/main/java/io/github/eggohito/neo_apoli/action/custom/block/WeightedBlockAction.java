package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

public record WeightedBlockAction(ShufflingList<BlockAction> entries) implements BlockAction, WeightedMetaAction<BlockAction> {

	public static final MapCodec<WeightedBlockAction> CODEC = MapCodecUtil.lazy(WeightedBlockAction.class.getSimpleName(), () -> WeightedMetaAction.createCodec(BlockAction.CODEC, WeightedBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedBlockAction.class.getSimpleName(), () -> WeightedMetaAction.createStreamCodec(BlockAction.STREAM_CODEC, WeightedBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.WEIGHTED;
	}

	@Override
	public String asDisplayString() {
		return BlockAction.super.asDisplayString();
	}

}
