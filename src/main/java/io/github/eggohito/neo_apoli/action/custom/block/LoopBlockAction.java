package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record LoopBlockAction(Optional<BlockAction> beforeAction, Optional<BlockAction> afterAction, NumberProvider iterations, BlockAction action) implements BlockAction, LoopMetaAction<BlockAction> {

	public static final MapCodec<LoopBlockAction> MAP_CODEC = MapCodecUtil.lazy(LoopBlockAction.class.getSimpleName(), () -> LoopMetaAction.mapCodec(BlockAction.CODEC, LoopBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, LoopBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(LoopBlockAction.class.getSimpleName(), () -> LoopMetaAction.streamCodec(BlockAction.STREAM_CODEC, LoopBlockAction::new));

	@Override
	public BlockAction.Type<?> getType() {
		return NeoApoliBlockActionTypes.LOOP;
	}

}
