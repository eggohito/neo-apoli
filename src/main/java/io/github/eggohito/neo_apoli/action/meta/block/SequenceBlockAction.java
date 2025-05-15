package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.SequenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record SequenceBlockAction(List<BlockAction> actions) implements BlockAction, SequenceMetaAction<BlockAction, BlockActionType<?>> {

	public static final MapCodec<SequenceBlockAction> CODEC = NeoApoliCodecs.lazyMap("SequenceBlockAction", () -> SequenceMetaAction.codec(BlockAction.CODEC, SequenceBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, SequenceBlockAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy("SequenceBlockAction", () -> SequenceMetaAction.packetCodec(BlockAction.PACKET_CODEC, SequenceBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.SEQUENCE;
	}

}
