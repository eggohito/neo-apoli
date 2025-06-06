package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface ItemAction extends Action<ItemActionType<?>> {

	Codec<ItemAction> CODEC = Codec.recursive(ItemAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(ItemActionTypes.CODEC.dispatch(TYPE_KEY, ItemAction::getType, ItemActionType::mapCodec)));
	PacketCodec<RegistryByteBuf, ItemAction> PACKET_CODEC = ItemActionTypes.PACKET_CODEC.dispatch(ItemAction::getType, ItemActionType::packetCodec);

	@Override
	default ActionCategory<ItemAction> getCategory() {
		return ActionCategories.ITEM_ACTION;
	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.ITEM_STACK);
	}

	@Override
	default String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.ITEM_ACTION_TYPE, this.getType()) + "\"";
	}

}
