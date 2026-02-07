package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IConditionalMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ConditionalItemAction(ItemCondition condition, ItemAction ifAction, Optional<ItemAction> elseAction) implements ItemAction, IConditionalMetaAction<ItemCondition, ItemAction> {

	public static final MapCodec<ConditionalItemAction> MAP_CODEC = MapCodecUtil.lazy(ConditionalItemAction.class.getSimpleName(), () -> IConditionalMetaAction.mapCodec(ItemCondition.CODEC, ItemAction.CODEC, ConditionalItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ConditionalItemAction> STREAM_CODEC = StreamCodecUtil.lazy(ConditionalItemAction.class.getSimpleName(), () -> IConditionalMetaAction.streamCodec(ItemCondition.STREAM_CODEC, ItemAction.STREAM_CODEC, ConditionalItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.CONDITIONAL;
	}

}
