package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.meta.block.SequenceBlockAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface BlockAction extends Action<BlockActionType<?>> {

	Codec<BlockAction> CODEC = Codec.recursive("BlockAction", codec -> Codec.withAlternative(BlockActionTypes.CODEC.dispatch(TYPE_KEY, BlockAction::getType, BlockActionType::mapCodec), codec.listOf().xmap(SequenceBlockAction::new, SequenceBlockAction::actions)));
	PacketCodec<RegistryByteBuf, BlockAction> PACKET_CODEC = BlockActionTypes.PACKET_CODEC.dispatch(BlockAction::getType, BlockActionType::packetCodec);

	@Override
	default String asDisplayString() {
		return "Block action (with type \"" + RegistryUtil.getId(NeoApoliRegistries.BLOCK_ACTION_TYPE, this.getType()) + "\")";
	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

}
