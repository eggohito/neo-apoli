package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceItemAction(List<Case<ItemCondition, ItemAction>> cases, ItemAction defaultAction) implements ItemAction, IChoiceMetaAction<ItemCondition, ItemAction> {

	public static final MapCodec<ChoiceItemAction> MAP_CODEC = MapCodecUtil.lazy(ChoiceItemAction.class.getSimpleName(), () -> IChoiceMetaAction.mapCodec(ItemCondition.CODEC, ItemAction.CODEC, ChoiceItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceItemAction> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceItemAction.class.getSimpleName(), () -> IChoiceMetaAction.streamCodec(ItemCondition.STREAM_CODEC, ItemAction.STREAM_CODEC, ChoiceItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.CHOICE;
	}

}
