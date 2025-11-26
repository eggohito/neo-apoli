package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ChoiceMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ChoiceItemAction(List<Case<ItemCondition, ItemAction>> cases, ItemAction defaultAction) implements ItemAction, ChoiceMetaAction<ItemCondition, ItemAction> {

	public static final MapCodec<ChoiceItemAction> CODEC = MapCodecUtil.lazy(ChoiceItemAction.class.getSimpleName(), () -> ChoiceMetaAction.createCodec(ItemCondition.CODEC, ItemAction.CODEC, ChoiceItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChoiceItemAction> STREAM_CODEC = StreamCodecUtil.lazy(ChoiceItemAction.class.getSimpleName(), () -> ChoiceMetaAction.createStreamCodec(ItemCondition.STREAM_CODEC, ItemAction.STREAM_CODEC, ChoiceItemAction::new));

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
