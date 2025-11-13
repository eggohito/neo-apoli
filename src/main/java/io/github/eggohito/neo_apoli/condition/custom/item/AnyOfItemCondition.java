package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import lombok.Data;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AnyOfItemCondition(List<ItemCondition> conditions) implements ItemCondition, AnyOfMetaCondition<ItemCondition> {

	public static final MapCodec<AnyOfItemCondition> CODEC = MapCodecUtil.lazy(AnyOfItemCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(ItemCondition.CODEC, AnyOfItemCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfItemCondition> PACKET_CODEC = PacketCodecUtil.lazy(AnyOfItemCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(ItemCondition.PACKET_CODEC, AnyOfItemCondition::new));

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.ANY_OF;
	}

	@Override
	public String asDisplayString() {
		return ItemCondition.super.asDisplayString();
	}

}
