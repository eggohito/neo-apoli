package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedItemCondition(ItemCondition condition) implements ItemCondition, InvertedMetaCondition<ItemCondition, ItemConditionType<?>> {

	public static final MapCodec<InvertedItemCondition> CODEC = NeoApoliMapCodecs.lazy(InvertedItemCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(ItemCondition.CODEC, InvertedItemCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedItemCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(InvertedItemCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(ItemCondition.PACKET_CODEC, InvertedItemCondition::new));

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.INVERTED;
	}

}
