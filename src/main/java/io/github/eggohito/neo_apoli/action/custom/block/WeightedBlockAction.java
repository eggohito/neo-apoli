package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

public record WeightedBlockAction(WeightedList<BlockAction> entries) implements BlockAction, WeightedMetaAction<BlockAction> {

	public static final MapCodec<WeightedBlockAction> CODEC = MapCodecUtil.lazy(WeightedBlockAction.class.getSimpleName(), () -> WeightedMetaAction.codec(BlockAction.CODEC, WeightedBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, WeightedBlockAction> PACKET_CODEC = PacketCodecUtil.lazy(WeightedBlockAction.class.getSimpleName(), () -> WeightedMetaAction.packetCodec(BlockAction.PACKET_CODEC, WeightedBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.WEIGHTED;
	}

	@Override
	public void execute(Context context) {
		WeightedMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		WeightedMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return BlockAction.super.asDisplayString();
	}

}
