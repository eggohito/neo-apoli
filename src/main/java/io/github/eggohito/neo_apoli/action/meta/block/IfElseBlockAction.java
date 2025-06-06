package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record IfElseBlockAction(BlockCondition condition, BlockAction ifAction, Optional<BlockAction> elseAction) implements BlockAction, IfElseMetaAction<BlockAction, BlockCondition, BlockActionType<?>, BlockConditionType<?>> {

	public static final MapCodec<IfElseBlockAction> CODEC = NeoApoliMapCodecs.lazy(IfElseBlockAction.class.getSimpleName(), () -> IfElseMetaAction.codec(BlockCondition.CODEC, BlockAction.CODEC, IfElseBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseBlockAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(IfElseBlockAction.class.getSimpleName(), () -> IfElseMetaAction.packetCodec(BlockCondition.PACKET_CODEC, BlockAction.PACKET_CODEC, IfElseBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.IF_ELSE;
	}

}
