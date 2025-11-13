package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ConditionalMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record ConditionalBlockAction(BlockCondition condition, BlockAction ifAction, Optional<BlockAction> elseAction) implements BlockAction, ConditionalMetaAction<BlockCondition, BlockAction> {

	public static final MapCodec<ConditionalBlockAction> CODEC = MapCodecUtil.lazy(ConditionalBlockAction.class.getSimpleName(), () -> ConditionalMetaAction.codec(BlockCondition.CODEC, BlockAction.CODEC, ConditionalBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, ConditionalBlockAction> PACKET_CODEC = PacketCodecUtil.lazy(ConditionalBlockAction.class.getSimpleName(), () -> ConditionalMetaAction.packetCodec(BlockCondition.PACKET_CODEC, BlockAction.PACKET_CODEC, ConditionalBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.CONDITIONAL;
	}

	@Override
	public void execute(Context context) {
		ConditionalMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		ConditionalMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return BlockAction.super.asDisplayString();
	}

}
