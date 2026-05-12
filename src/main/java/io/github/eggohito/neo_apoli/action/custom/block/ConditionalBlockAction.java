package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ConditionalMetaAction;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliBlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalBlockAction(BlockCondition condition, BlockAction ifAction, Optional<BlockAction> elseAction) implements BlockAction, ConditionalMetaAction<BlockCondition, BlockAction> {

	public static final MapCodec<ConditionalBlockAction> MAP_CODEC = MapCodecUtil.lazy(ConditionalBlockAction.class.getSimpleName(), () -> ConditionalMetaAction.mapCodec(BlockCondition.CODEC, BlockAction.CODEC, ConditionalBlockAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalBlockAction> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalBlockAction.class.getSimpleName(), () -> ConditionalMetaAction.streamCodec(BlockCondition.STREAM_CODEC, BlockAction.STREAM_CODEC, ConditionalBlockAction::new));

	@Override
	public BlockAction.Type<?> getType() {
		return NeoApoliBlockActionTypes.CONDITIONAL;
	}

}
