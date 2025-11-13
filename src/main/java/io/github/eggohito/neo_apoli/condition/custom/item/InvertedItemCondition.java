package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import lombok.Data;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedItemCondition(ItemCondition condition) implements ItemCondition, InvertedMetaCondition<ItemCondition> {

	public static final MapCodec<InvertedItemCondition> CODEC = MapCodecUtil.lazy(InvertedItemCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(ItemCondition.CODEC, InvertedItemCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedItemCondition> PACKET_CODEC = PacketCodecUtil.lazy(InvertedItemCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(ItemCondition.PACKET_CODEC, InvertedItemCondition::new));

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return ItemCondition.super.asDisplayString();
	}

}
