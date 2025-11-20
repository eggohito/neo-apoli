package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record IsFoodItemCondition() implements ItemCondition {

	public static final MapCodec<IsFoodItemCondition> CODEC = MapCodec.unit(IsFoodItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsFoodItemCondition> PACKET_CODEC = PacketCodecUtil.unit(IsFoodItemCondition::new);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.IS_FOOD;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextParameters.ITEM_STACK)
			.stream()
			.anyMatch(stack -> stack.contains(DataComponentTypes.FOOD));
	}

}
