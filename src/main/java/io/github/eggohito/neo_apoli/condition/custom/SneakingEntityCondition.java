package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.context.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class SneakingEntityCondition extends EntityCondition {

	public static final MapCodec<SneakingEntityCondition> CODEC = createSimpleCodec(SneakingEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, SneakingEntityCondition> PACKET_CODEC = createSimplePacketCodec(SneakingEntityCondition::new);

	public SneakingEntityCondition(boolean inverted) {
		super(inverted);
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.SNEAKING;
	}

	@Override
	protected boolean check(EntityConditionContext context) {
		return context.entity().isSneaking();
	}

}
