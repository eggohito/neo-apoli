package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record SequenceBlockAction(List<BlockAction> actions) implements BlockAction, SequenceMetaAction<BlockAction> {

	public static final MapCodec<SequenceBlockAction> CODEC = MapCodecUtil.lazy(SequenceBlockAction.class.getSimpleName(), () -> SequenceMetaAction.codec(BlockAction.CODEC, SequenceBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceBlockAction> PACKET_CODEC = PacketCodecUtil.lazy(SequenceBlockAction.class.getSimpleName(), () -> SequenceMetaAction.packetCodec(BlockAction.PACKET_CODEC, SequenceBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.SEQUENCE;
	}

	@Override
	public void execute(Context context) {
		SequenceMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		SequenceMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return BlockAction.super.asDisplayString();
	}

}
