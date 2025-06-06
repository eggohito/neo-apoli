package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface ItemCondition extends Condition<ItemConditionType<?>> {

	Codec<ItemCondition> CODEC = ItemConditionTypes.CODEC.dispatch(TYPE_KEY, ItemCondition::getType, ItemConditionType::mapCodec);
	PacketCodec<RegistryByteBuf, ItemCondition> PACKET_CODEC = ItemConditionTypes.PACKET_CODEC.dispatch(ItemCondition::getType, ItemConditionType::packetCodec);

	@Override
	default ConditionCategory<ItemCondition> getCategory() {
		return ConditionCategories.ITEM_CONDITION;
	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.ITEM_STACK);
	}

	@Override
	default String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.ITEM_CONDITION_TYPE, this.getType()) + "\"";
	}

}
