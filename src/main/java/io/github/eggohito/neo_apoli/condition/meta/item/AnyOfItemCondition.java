package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AnyOfItemCondition(List<ItemCondition> conditions) implements ItemCondition, AnyOfMetaCondition<ItemCondition, ItemConditionType<?>> {

	public static final MapCodec<AnyOfItemCondition> CODEC = NeoApoliMapCodecs.lazy(AnyOfItemCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(ItemCondition.CODEC, AnyOfItemCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfItemCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(AnyOfItemCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(ItemCondition.PACKET_CODEC, AnyOfItemCondition::new));

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.ANY_OF;
	}

}
