package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class IsEmptyItemCondition extends ItemCondition {

	public static final MapCodec<IsEmptyItemCondition> CODEC = MapCodec.unit(IsEmptyItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsEmptyItemCondition> PACKET_CODEC = PacketCodec.unit(new IsEmptyItemCondition());

	public IsEmptyItemCondition() {

	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.IS_EMPTY;
	}

	@Override
	protected boolean impl(Context context) {
		return context.required(ContextParameters.ITEM_STACK).isEmpty();
	}

}
