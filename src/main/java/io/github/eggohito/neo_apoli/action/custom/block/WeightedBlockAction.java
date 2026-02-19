package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.WeightedList;

public record WeightedBlockAction(WeightedList<BlockAction> entries) implements BlockAction, WeightedMetaAction<BlockAction> {

	public static final MapCodec<WeightedBlockAction> MAP_CODEC = MapCodecUtil.lazy(WeightedBlockAction.class.getSimpleName(), () -> WeightedMetaAction.mapCodec(BlockAction.CODEC, WeightedBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedBlockAction.class.getSimpleName(), () -> WeightedMetaAction.streamCodec(BlockAction.STREAM_CODEC, WeightedBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.WEIGHTED;
	}

}
