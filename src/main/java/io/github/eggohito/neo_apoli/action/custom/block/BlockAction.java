package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface BlockAction extends Action {

	Codec<BlockAction> CODEC = Codec.recursive(BlockAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BlockActionType.CODEC.dispatch(BlockAction::getType, BlockActionType::mapCodec), codec.listOf().xmap(SequenceBlockAction::new, SequenceBlockAction::actions)));

	StreamCodec<RegistryFriendlyByteBuf, BlockAction> STREAM_CODEC = BlockActionType.STREAM_CODEC.dispatch(BlockAction::getType, BlockActionType::streamCodec);

	@Override
	BlockActionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.BLOCK_POS);
	}

}
