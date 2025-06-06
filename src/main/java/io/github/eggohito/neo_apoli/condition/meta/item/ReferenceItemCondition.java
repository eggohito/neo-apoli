package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceItemCondition(Identifier value) implements ItemCondition, ReferenceMetaCondition<ItemCondition, ItemConditionType<?>> {

	public static final MapCodec<ReferenceItemCondition> CODEC = ReferenceMetaCondition.codec(ReferenceItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceItemCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceItemCondition::new);

	@Override
	public ConditionCategory<ItemCondition> getCategory() {
		return ItemCondition.super.getCategory();
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.REFERENCE;
	}

}
