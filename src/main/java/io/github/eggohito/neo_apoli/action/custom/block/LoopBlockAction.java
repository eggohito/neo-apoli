package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record LoopBlockAction(Optional<BlockAction> beforeAction, Optional<BlockAction> afterAction, NumberProvider iterations, BlockAction action) implements BlockAction, LoopMetaAction<BlockAction> {

	public static final MapCodec<LoopBlockAction> CODEC = MapCodecUtil.lazy(LoopBlockAction.class.getSimpleName(), () -> LoopMetaAction.codec(BlockAction.CODEC, LoopBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, LoopBlockAction> PACKET_CODEC = PacketCodecUtil.lazy(LoopBlockAction.class.getSimpleName(), () -> LoopMetaAction.packetCodec(BlockAction.PACKET_CODEC, LoopBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.LOOP;
	}

	@Override
	public void execute(Context context) {
		LoopMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		LoopMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return BlockAction.super.asDisplayString();
	}

}
