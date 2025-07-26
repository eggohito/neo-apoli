package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.meta.block.SequenceBlockAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public abstract class BlockAction extends Action {

	public static final MapCodec<BlockAction> MAP_CODEC = BlockActionTypes.CODEC.dispatchMap("type", BlockAction::getType, BlockActionType::mapCodec);
	public static final Codec<BlockAction> BASE_CODEC = MAP_CODEC.codec();

	public static final Codec<BlockAction> CODEC = Codec.recursive(BlockAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BASE_CODEC, codec.listOf().xmap(SequenceBlockAction::new, SequenceBlockAction::actions)));
	public static final PacketCodec<RegistryByteBuf, BlockAction> PACKET_CODEC = BlockActionTypes.PACKET_CODEC.dispatch(BlockAction::getType, BlockActionType::packetCodec);

	@Override
	public abstract BlockActionType<?> getType();

	@Override
	public ActionCategory<BlockAction> getCategory() {
		return ActionCategories.BLOCK_ACTION;
	}

	protected final void impl(Context context) {

		if (context.getWorld() instanceof ServerWorld serverWorld) {

			BlockPos blockPos = context.required(ContextParameters.BLOCK_POS);
			ServerContext serverContext = new ServerContext(context, serverWorld).copy(builder -> builder.add(ContextParameters.POSITION, blockPos.toCenterPos()));

			this.impl(serverContext);

		}

	}

	protected abstract void impl(ServerContext context);

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return ContextTypes.BLOCK.getAllowed();
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.BLOCK_ACTION_TYPE, this.getType()) + "\"";
	}

}
