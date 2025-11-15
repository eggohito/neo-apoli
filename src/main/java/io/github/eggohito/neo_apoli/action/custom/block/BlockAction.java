package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface BlockAction extends Action {

	Codec<BlockAction> CODEC = Codec.recursive(BlockAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BlockActionType.CODEC.dispatch(BlockAction::getType, BlockActionType::mapCodec), codec.listOf().xmap(SequenceBlockAction::new, SequenceBlockAction::actions), NothingBlockAction.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, BlockAction> PACKET_CODEC = BlockActionType.PACKET_CODEC.dispatch(BlockAction::getType, BlockActionType::packetCodec);

	@Override
	BlockActionType<?> getType();

	@Override
	default void execute(Context context) {

		if (context.getWorld() instanceof ServerWorld serverWorld && !serverWorld.isDebugWorld()) {
			this.serverExecute(new ServerContext.Builder(context).build(serverWorld));
		}

	}

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.BLOCK_POS);
	}

	@Override
	default String asDisplayString() {
		return "Block action with type \"" + RegistryUtil.getId(NeoApoliRegistries.ACTION_TYPE, this.getType()) + "\"";
	}

	void serverExecute(ServerContext context);

}
