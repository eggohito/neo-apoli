package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.SideMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record SideBlockAction(BlockAction action, SideMetaAction.Side side) implements BlockAction, SideMetaAction<BlockAction, BlockActionType<?>> {

	public static final MapCodec<SideBlockAction> CODEC = NeoApoliCodecs.lazyMap("SideBlockAction", () -> SideMetaAction.createCodec(BlockAction.CODEC, SideBlockAction::new));
	public static final PacketCodec<RegistryByteBuf, SideBlockAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy("SideBlockAction", () -> SideMetaAction.createPacketCodec(BlockAction.PACKET_CODEC, SideBlockAction::new));

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.SIDE;
	}

}
