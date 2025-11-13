package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import lombok.Data;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceItemCondition(Identifier value) implements ItemCondition, ReferenceMetaCondition<ItemCondition> {

	public static final MapCodec<ReferenceItemCondition> CODEC = ReferenceMetaCondition.codec(ReferenceItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceItemCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceItemCondition::new);

	@Override
	public Pair<Class<ItemCondition>, String> classAndName() {
		return Pair.of(ItemCondition.class, "Item condition");
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.REFERENCE;
	}

	@Override
	public String asDisplayString() {
		return ItemCondition.super.asDisplayString();
	}

}
