package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IWeightedMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.behavior.ShufflingList;

public record WeightedItemAction(ShufflingList<ItemAction> entries) implements ItemAction, IWeightedMetaAction<ItemAction> {

	public static final MapCodec<WeightedItemAction> CODEC = MapCodecUtil.lazy(WeightedItemAction.class.getSimpleName(), () -> IWeightedMetaAction.createCodec(ItemAction.CODEC, WeightedItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WeightedItemAction> STREAM_CODEC = StreamCodecUtil.lazy(WeightedItemAction.class.getSimpleName(), () -> IWeightedMetaAction.createStreamCodec(ItemAction.STREAM_CODEC, WeightedItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.WEIGHTED;
	}

	@Override
	public String asDisplayString() {
		return ItemAction.super.asDisplayString();
	}

}
