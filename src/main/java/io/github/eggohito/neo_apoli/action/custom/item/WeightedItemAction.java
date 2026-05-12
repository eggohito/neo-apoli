package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.WeightedMetaAction;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliItemActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.WeightedList;

public record WeightedItemAction(WeightedList<ItemAction> entries) implements ItemAction, WeightedMetaAction<ItemAction> {

	public static final MapCodec<WeightedItemAction> MAP_CODEC = MapCodecUtil.lazy(WeightedItemAction.class.getSimpleName(), () -> WeightedMetaAction.mapCodec(ItemAction.CODEC, WeightedItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedItemAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedItemAction.class.getSimpleName(), () -> WeightedMetaAction.streamCodec(ItemAction.STREAM_CODEC, WeightedItemAction::new));

	@Override
	public ItemAction.Type<?> getType() {
		return NeoApoliItemActionTypes.WEIGHTED;
	}

}
