package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.NothingMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record NothingItemAction() implements ItemAction, NothingMetaAction<ItemActionType<?>> {

	public static final MapCodec<NothingItemAction> CODEC = MapCodec.unit(NothingItemAction::new);
	public static final PacketCodec<RegistryByteBuf, NothingItemAction> PACKET_CODEC = PacketCodec.unit(new NothingItemAction());

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.NOTHING;
	}

}
