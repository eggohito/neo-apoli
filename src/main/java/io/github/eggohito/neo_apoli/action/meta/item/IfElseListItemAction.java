package io.github.eggohito.neo_apoli.action.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.meta.IfElseListMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record IfElseListItemAction(List<Entry<ItemCondition, ItemAction>> entries) implements ItemAction, IfElseListMetaAction<ItemAction, ItemCondition, ItemActionType<?>, ItemConditionType<?>> {

	public static final MapCodec<IfElseListItemAction> CODEC = NeoApoliMapCodecs.lazy(IfElseListItemAction.class.getSimpleName(), () -> IfElseListMetaAction.codec(ItemCondition.CODEC, ItemAction.CODEC, IfElseListItemAction::new));
	public static final PacketCodec<RegistryByteBuf, IfElseListItemAction> PACKET_CODEC = NeoApoliPacketCodecs.lazy(IfElseListItemAction.class.getSimpleName(), () -> IfElseListMetaAction.packetCodec(ItemCondition.PACKET_CODEC, ItemAction.PACKET_CODEC, IfElseListItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.IF_ELSE_LIST;
	}

}
