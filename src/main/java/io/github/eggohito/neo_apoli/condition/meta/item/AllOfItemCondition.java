package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfItemCondition(List<ItemCondition> conditions) implements ItemCondition, AllOfMetaCondition<ItemCondition, ItemConditionType<?>> {

	public static final MapCodec<AllOfItemCondition> CODEC = NeoApoliMapCodecs.lazy(AllOfItemCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(ItemCondition.CODEC, AllOfItemCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfItemCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(AllOfItemCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(ItemCondition.PACKET_CODEC, AllOfItemCondition::new));

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.ALL_OF;
	}

}
