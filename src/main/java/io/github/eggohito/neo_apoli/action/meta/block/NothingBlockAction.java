package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record NothingBlockAction() implements BlockAction, NothingMetaAction<BlockActionType<?>> {

	public static final MapCodec<NothingBlockAction> CODEC = MapCodec.unit(NothingBlockAction::new);
	public static final PacketCodec<RegistryByteBuf, NothingBlockAction> PACKET_CODEC = PacketCodec.unit(new NothingBlockAction());

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.NOTHING;
	}

}
