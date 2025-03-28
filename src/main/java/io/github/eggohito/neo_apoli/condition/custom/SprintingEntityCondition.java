package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.context.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class SprintingEntityCondition extends EntityCondition {

	public static final MapCodec<SprintingEntityCondition> CODEC = createSimpleCodec(SprintingEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, SprintingEntityCondition> PACKET_CODEC = createSimplePacketCodec(SprintingEntityCondition::new);

	public SprintingEntityCondition(boolean inverted) {
		super(inverted);
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.SPRINTING;
	}

	@Override
	protected boolean check(EntityConditionContext context) {
		return context.entity().isSprinting();
	}

}
