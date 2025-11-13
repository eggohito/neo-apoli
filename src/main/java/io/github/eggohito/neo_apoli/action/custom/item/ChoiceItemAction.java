package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record ChoiceItemAction(List<Case<ItemCondition, ItemAction>> cases, ItemAction defaultAction) implements ItemAction, ChoiceMetaAction<ItemCondition, ItemAction> {

	public static final MapCodec<ChoiceItemAction> CODEC = MapCodecUtil.lazy(ChoiceItemAction.class.getSimpleName(), () -> ChoiceMetaAction.codec(ItemCondition.CODEC, ItemAction.CODEC, ChoiceItemAction::new));
	public static final PacketCodec<RegistryByteBuf, ChoiceItemAction> PACKET_CODEC = PacketCodecUtil.lazy(ChoiceItemAction.class.getSimpleName(), () -> ChoiceMetaAction.packetCodec(ItemCondition.PACKET_CODEC, ItemAction.PACKET_CODEC, ChoiceItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.CHOICE;
	}

	@Override
	public void execute(Context context) {
		ChoiceMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		ChoiceMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return ItemAction.super.asDisplayString();
	}

}
