package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.context.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class IsSprintingEntityConditionType extends EntityCondition {

	public static final MapCodec<IsSprintingEntityConditionType> CODEC = createSimpleCodec(IsSprintingEntityConditionType::new);
	public static final PacketCodec<RegistryByteBuf, IsSprintingEntityConditionType> PACKET_CODEC = createSimplePacketCodec(IsSprintingEntityConditionType::new);

	public IsSprintingEntityConditionType(boolean inverted) {
		super(inverted);
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_SPRINTING;
	}

	@Override
	protected boolean check(EntityConditionContext context) {
		return context.entity().isSprinting();
	}

}
