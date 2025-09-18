package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

public abstract class ItemCondition extends Condition {

	public static final MapCodec<ItemCondition> MAP_CODEC = ItemConditionTypes.CODEC.dispatchMap("type", ItemCondition::getType, ItemConditionType::mapCodec);
	public static final Codec<ItemCondition> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, ItemCondition> PACKET_CODEC = ItemConditionTypes.PACKET_CODEC.dispatch(ItemCondition::getType, ItemConditionType::packetCodec);

	@Override
	public abstract ItemConditionType<?> getType();

	@Override
	public ConditionCategory<ItemCondition> getCategory() {
		return ConditionCategories.ITEM_CONDITION;
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.ITEM_STACK);
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.ITEM_CONDITION_TYPE, this.getType()) + "\"";
	}

}
