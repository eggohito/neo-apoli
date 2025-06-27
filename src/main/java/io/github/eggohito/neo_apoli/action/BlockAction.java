package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
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
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class BlockAction extends Action {

	public static final Codec<BlockAction> CODEC = Codec.recursive(BlockAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BlockActionTypes.CODEC.dispatch("type", BlockAction::getType, BlockActionType::mapCodec), codec.listOf().xmap(SequenceBlockAction::new, SequenceBlockAction::actions)));
	public static final PacketCodec<RegistryByteBuf, BlockAction> PACKET_CODEC = BlockActionTypes.PACKET_CODEC.dispatch(BlockAction::getType, BlockActionType::packetCodec);

	@Override
	public abstract BlockActionType<?> getType();

	@Override
	public ActionCategory<BlockAction> getCategory() {
		return ActionCategories.BLOCK_ACTION;
	}

	@Override
	public final void impl(Context context) {

		if (context.getWorld() instanceof ServerWorld serverWorld) {
			this.impl(new ServerContext(context, serverWorld));
		}

	}

	protected abstract void impl(ServerContext context);

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.BLOCK_ACTION_TYPE, this.getType()) + "\"";
	}

	protected BlockPos getBlockPos(Context context) {
		return BlockPos.ofFloored(context.required(ContextParameters.POSITION));
	}

	protected BlockState getBlockState(Context context) {
		return context.optional(ContextParameters.BLOCK_STATE).orElseGet(() -> context.getWorld().getBlockState(this.getBlockPos(context)));
	}

	@Nullable
	protected BlockEntity getBlockEntity(Context context) {
		return context.optional(ContextParameters.BLOCK_ENTITY).orElseGet(() -> context.getWorld().getBlockEntity(this.getBlockPos(context)));
	}

}
