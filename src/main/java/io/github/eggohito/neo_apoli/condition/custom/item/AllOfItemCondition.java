package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import lombok.Data;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfItemCondition(List<ItemCondition> conditions) implements ItemCondition, AllOfMetaCondition<ItemCondition> {

	public static final MapCodec<AllOfItemCondition> CODEC = MapCodecUtil.lazy(AllOfItemCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(ItemCondition.CODEC, AllOfItemCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfItemCondition> PACKET_CODEC = PacketCodecUtil.lazy(AllOfItemCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(ItemCondition.PACKET_CODEC, AllOfItemCondition::new));

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.ALL_OF;
	}

	@Override
	public String asDisplayString() {
		return ItemCondition.super.asDisplayString();
	}

}
