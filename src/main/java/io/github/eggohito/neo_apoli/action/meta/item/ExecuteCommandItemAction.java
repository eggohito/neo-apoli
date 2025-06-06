package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.ExecuteCommandMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ExecuteCommandItemAction(StringProvider command) implements ItemAction, ExecuteCommandMetaAction<ItemActionType<?>> {

	public static final MapCodec<ExecuteCommandItemAction> CODEC = ExecuteCommandMetaAction.codec(ExecuteCommandItemAction::new);
	public static final PacketCodec<RegistryByteBuf, ExecuteCommandItemAction> PACKET_CODEC = ExecuteCommandMetaAction.packetCodec(ExecuteCommandItemAction::new);

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.EXECUTE_COMMAND;
	}

}
