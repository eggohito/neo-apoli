package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantItemCondition(boolean value) implements ItemCondition, ConstantMetaCondition<ItemConditionType<?>> {

	public static final MapCodec<ConstantItemCondition> CODEC = ConstantMetaCondition.codec(ConstantItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, ConstantItemCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantItemCondition::new).cast();

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.CONSTANT;
	}

}
