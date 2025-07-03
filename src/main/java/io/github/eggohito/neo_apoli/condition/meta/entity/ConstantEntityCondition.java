package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class ConstantEntityCondition extends EntityCondition implements ConstantMetaCondition {

	public static final MapCodec<ConstantEntityCondition> CODEC = ConstantMetaCondition.codec(ConstantEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, ConstantEntityCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantEntityCondition::new).cast();

	private final boolean value;

	public ConstantEntityCondition(boolean value) {
		this.value = value;
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.CONSTANT;
	}

	@Override
	protected boolean impl(Context context) {
		return value();
	}

}
