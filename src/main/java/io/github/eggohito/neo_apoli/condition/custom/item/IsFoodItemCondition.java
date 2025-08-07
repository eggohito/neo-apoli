package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class IsFoodItemCondition extends ItemCondition {

	public static final MapCodec<IsFoodItemCondition> CODEC = MapCodec.unit(IsFoodItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsFoodItemCondition> PACKET_CODEC = PacketCodec.unit(new IsFoodItemCondition());

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.IS_FOOD;
	}

	@Override
	protected boolean impl(Context context) {
		return context.required(ContextParameters.ITEM_STACK).contains(DataComponentTypes.FOOD);
	}

}
