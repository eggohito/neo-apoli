package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import lombok.Data;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantItemCondition(boolean value) implements ItemCondition, ConstantMetaCondition {

	public static final Codec<ConstantItemCondition> INLINE_CODEC = ConstantMetaCondition.inlineCodec(ConstantItemCondition::new);

	public static final MapCodec<ConstantItemCondition> CODEC = ConstantMetaCondition.codec(ConstantItemCondition::new);

	public static final PacketCodec<RegistryByteBuf, ConstantItemCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.CONSTANT;
	}

	@Override
	public String asDisplayString() {
		return ItemCondition.super.asDisplayString();
	}

}
