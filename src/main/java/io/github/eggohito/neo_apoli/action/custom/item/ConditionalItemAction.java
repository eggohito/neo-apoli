package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ConditionalMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalItemAction(ItemCondition condition, ItemAction ifAction, Optional<ItemAction> elseAction) implements ItemAction, ConditionalMetaAction<ItemCondition, ItemAction> {

	public static final MapCodec<ConditionalItemAction> CODEC = MapCodecUtil.lazy(ConditionalItemAction.class.getSimpleName(), () -> ConditionalMetaAction.createCodec(ItemCondition.CODEC, ItemAction.CODEC, ConditionalItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalItemAction> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalItemAction.class.getSimpleName(), () -> ConditionalMetaAction.createStreamCodec(ItemCondition.STREAM_CODEC, ItemAction.STREAM_CODEC, ConditionalItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.CONDITIONAL;
	}

	@Override
	public void execute(Context context) {
		ConditionalMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		ConditionalMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return ItemAction.super.asDisplayString();
	}

}
