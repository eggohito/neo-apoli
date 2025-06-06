package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record CompareItemCondition(Comparison comparison) implements ItemCondition, CompareMetaCondition<ItemConditionType<?>> {

	public static final MapCodec<CompareItemCondition> CODEC = CompareMetaCondition.codec(CompareItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareItemCondition> PACKET_CODEC = CompareMetaCondition.packetCodec(CompareItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.COMPARE;
	}

}
