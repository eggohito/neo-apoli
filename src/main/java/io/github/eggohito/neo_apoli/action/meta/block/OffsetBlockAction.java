package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.OffsetMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public record OffsetBlockAction(BlockAction action, Vec3d offset) implements BlockAction, OffsetMetaAction<BlockAction, BlockActionType<?>> {

	public static final MapCodec<OffsetBlockAction> CODEC = OffsetMetaAction.createCodec(BlockAction.CODEC, OffsetBlockAction::new);
	public static final PacketCodec<RegistryByteBuf, OffsetBlockAction> PACKET_CODEC = OffsetMetaAction.createPacketCodec(BlockAction.PACKET_CODEC, OffsetBlockAction::new);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.OFFSET;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return BlockAction.super.getAllowedParameters();
	}

}
