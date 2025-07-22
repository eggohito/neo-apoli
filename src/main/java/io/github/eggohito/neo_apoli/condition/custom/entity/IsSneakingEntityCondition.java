package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class IsSneakingEntityCondition extends EntityCondition {

	public static final MapCodec<IsSneakingEntityCondition> CODEC = MapCodec.unit(IsSneakingEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsSneakingEntityCondition> PACKET_CODEC = PacketCodec.unit(new IsSneakingEntityCondition());

	public IsSneakingEntityCondition() {

	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_SNEAKING;
	}

	@Override
	protected boolean impl(Context context) {
		return context.required(ContextParameters.ENTITY).isSneaking();
	}

}
