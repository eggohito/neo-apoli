package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.collection.WeightedList;

public record WeightedItemAction(WeightedList<ItemAction> entries) implements ItemAction, WeightedMetaAction<ItemAction> {

	public static final MapCodec<WeightedItemAction> CODEC = MapCodecUtil.lazy(WeightedItemAction.class.getSimpleName(), () -> WeightedMetaAction.codec(ItemAction.CODEC, WeightedItemAction::new));
	public static final PacketCodec<RegistryByteBuf, WeightedItemAction> PACKET_CODEC = PacketCodecUtil.lazy(WeightedItemAction.class.getSimpleName(), () -> WeightedMetaAction.packetCodec(ItemAction.PACKET_CODEC, WeightedItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.WEIGHTED;
	}

	@Override
	public void execute(Context context) {
		WeightedMetaAction.super.execute(context);
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
