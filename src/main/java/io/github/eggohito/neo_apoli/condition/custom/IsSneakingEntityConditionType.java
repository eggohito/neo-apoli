package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.context.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.EntityConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class IsSneakingEntityConditionType extends EntityCondition {

	public static final MapCodec<IsSneakingEntityConditionType> CODEC = createSimpleCodec(IsSneakingEntityConditionType::new);
	public static final PacketCodec<RegistryByteBuf, IsSneakingEntityConditionType> PACKET_CODEC = createSimplePacketCodec(IsSneakingEntityConditionType::new);

	public IsSneakingEntityConditionType(boolean inverted) {
		super(inverted);
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_SNEAKING;
	}

	@Override
	protected boolean check(EntityConditionContext context) {
		return context.entity().isSneaking();
	}

}
