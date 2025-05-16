package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.meta.ExecuteCommandMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ExecuteCommandBlockAction(StringProvider command) implements BlockAction, ExecuteCommandMetaAction<BlockActionType<?>> {

	public static final MapCodec<ExecuteCommandBlockAction> CODEC = ExecuteCommandMetaAction.codec(ExecuteCommandBlockAction::new);
	public static final PacketCodec<RegistryByteBuf, ExecuteCommandBlockAction> PACKET_CODEC = ExecuteCommandMetaAction.packetCodec(ExecuteCommandBlockAction::new);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.EXECUTE_COMMAND;
	}

}
