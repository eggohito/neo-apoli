package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.SwitchMetaAction;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliItemActionTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SwitchItemAction(List<Case<ItemCondition, ItemAction>> cases, ItemAction defaultAction) implements ItemAction, SwitchMetaAction<ItemCondition, ItemAction> {

	public static final MapCodec<SwitchItemAction> MAP_CODEC = MapCodecUtil.lazy(SwitchItemAction.class.getSimpleName(), () -> SwitchMetaAction.mapCodec(ItemCondition.CODEC, ItemAction.CODEC, SwitchItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchItemAction> STREAM_CODEC = StreamCodecUtil.lazy(SwitchItemAction.class.getSimpleName(), () -> SwitchMetaAction.streamCodec(ItemCondition.STREAM_CODEC, ItemAction.STREAM_CODEC, SwitchItemAction::new));

	@Override
	public ItemAction.Type<?> getType() {
		return NeoApoliItemActionTypes.SWITCH;
	}

}
