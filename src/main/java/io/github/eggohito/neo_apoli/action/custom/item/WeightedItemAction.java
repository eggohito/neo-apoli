package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IWeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.WeightedList;

public record WeightedItemAction(WeightedList<ItemAction> entries) implements ItemAction, IWeightedMetaAction<ItemAction> {

	public static final MapCodec<WeightedItemAction> MAP_CODEC = MapCodecUtil.lazy(WeightedItemAction.class.getSimpleName(), () -> IWeightedMetaAction.mapCodec(ItemAction.CODEC, WeightedItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedItemAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedItemAction.class.getSimpleName(), () -> IWeightedMetaAction.streamCodec(ItemAction.STREAM_CODEC, WeightedItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.WEIGHTED;
	}

}
