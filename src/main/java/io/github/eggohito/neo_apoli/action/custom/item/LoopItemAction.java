package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record LoopItemAction(Optional<ItemAction> beforeAction, Optional<ItemAction> afterAction, NumberProvider iterations, ItemAction action) implements ItemAction, LoopMetaAction<ItemAction> {

	public static final MapCodec<LoopItemAction> CODEC = MapCodecUtil.lazy(LoopItemAction.class.getSimpleName(), () -> LoopMetaAction.codec(ItemAction.CODEC, LoopItemAction::new));
	public static final PacketCodec<RegistryByteBuf, LoopItemAction> PACKET_CODEC = PacketCodecUtil.lazy(LoopItemAction.class.getSimpleName(), () -> LoopMetaAction.packetCodec(ItemAction.PACKET_CODEC, LoopItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.LOOP;
	}

	@Override
	public void execute(Context context) {
		LoopMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		this.execute(context);
	}

	@Override
	public String asDisplayString() {
		return ItemAction.super.asDisplayString();
	}

}
