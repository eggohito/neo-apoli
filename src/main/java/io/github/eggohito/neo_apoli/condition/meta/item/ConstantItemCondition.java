package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ConstantItemCondition extends ItemCondition implements ConstantMetaCondition {

	public static final MapCodec<ConstantItemCondition> CODEC = ConstantMetaCondition.codec(ConstantItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, ConstantItemCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantItemCondition::new).cast();

	private final boolean value;

	public ConstantItemCondition(boolean value) {
		this.value = value;
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.CONSTANT;
	}

	@Override
	protected boolean impl(Context context) {
		return value();
	}

}
