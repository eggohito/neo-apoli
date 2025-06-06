package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceItemAction(Identifier value) implements ItemAction, ReferenceMetaAction<ItemAction, ItemActionType<?>> {

	public static final MapCodec<ReferenceItemAction> CODEC = ReferenceMetaAction.codec(ReferenceItemAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceItemAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceItemAction::new);

	@Override
	public ActionCategory<ItemAction> getCategory() {
		return ItemAction.super.getCategory();
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.REFERENCE;
	}

}
