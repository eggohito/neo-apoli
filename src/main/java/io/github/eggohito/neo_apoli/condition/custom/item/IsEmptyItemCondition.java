package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record IsEmptyItemCondition() implements ItemCondition {

	public static final MapCodec<IsEmptyItemCondition> CODEC = MapCodec.unit(IsEmptyItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsEmptyItemCondition> PACKET_CODEC = PacketCodec.unit(new IsEmptyItemCondition());

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.IS_EMPTY;
	}

	@Override
	public boolean test(Context context) {
		return context.required(ContextParameters.ITEM_STACK).isEmpty();
	}

}
