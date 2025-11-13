package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record ChoiceBlockAction(List<Case<BlockCondition, BlockAction>> cases, BlockAction defaultAction) implements BlockAction, ChoiceMetaAction<BlockCondition, BlockAction> {

	public static final MapCodec<ChoiceBlockAction> CODEC = MapCodecUtil.lazy(ChoiceBlockAction.class.getSimpleName(), () -> ChoiceMetaAction.codec(BlockCondition.CODEC, BlockAction.CODEC, ChoiceBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, ChoiceBlockAction> PACKET_CODEC = PacketCodecUtil.lazy(ChoiceBlockAction.class.getSimpleName(), () -> ChoiceMetaAction.packetCodec(BlockCondition.PACKET_CODEC, BlockAction.PACKET_CODEC, ChoiceBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.CHOICE;
	}

	@Override
	public void execute(Context context) {
		ChoiceMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		ChoiceMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return BlockAction.super.asDisplayString();
	}

}
