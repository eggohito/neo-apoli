package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ILoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record LoopBlockAction(Optional<BlockAction> beforeAction, Optional<BlockAction> afterAction, NumberProvider iterations, BlockAction action) implements BlockAction, ILoopMetaAction<BlockAction> {

	public static final MapCodec<LoopBlockAction> MAP_CODEC = MapCodecUtil.lazy(LoopBlockAction.class.getSimpleName(), () -> ILoopMetaAction.mapCodec(BlockAction.CODEC, LoopBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, LoopBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(LoopBlockAction.class.getSimpleName(), () -> ILoopMetaAction.streamCodec(BlockAction.STREAM_CODEC, LoopBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.LOOP;
	}

}
