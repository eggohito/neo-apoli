package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseListMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record IfElseListBlockAction(List<Entry<BlockCondition, BlockAction>> entries) implements BlockAction, IfElseListMetaAction<BlockAction, BlockCondition, BlockActionType<?>, BlockConditionType<?>> {

	public static final MapCodec<IfElseListBlockAction> CODEC = IfElseListMetaAction.createCodec(BlockCondition.CODEC, BlockAction.CODEC, IfElseListBlockAction::new);
	public static final PacketCodec<RegistryByteBuf, IfElseListBlockAction> PACKET_CODEC = IfElseListMetaAction.createPacketCodec(BlockCondition.PACKET_CODEC, BlockAction.PACKET_CODEC, IfElseListBlockAction::new);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.IF_ELSE_LIST;
	}

}
