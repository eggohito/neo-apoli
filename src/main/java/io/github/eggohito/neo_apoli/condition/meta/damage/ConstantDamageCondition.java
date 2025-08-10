package io.github.eggohito.neo_apoli.condition.meta.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class ConstantDamageCondition extends DamageCondition implements ConstantMetaCondition {

	public static final MapCodec<ConstantDamageCondition> CODEC = ConstantMetaCondition.codec(ConstantDamageCondition::new);
	public static final PacketCodec<RegistryByteBuf, ConstantDamageCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantDamageCondition::new);

	private final boolean value;

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.CONSTANT;
	}

	@Override
	protected boolean impl(Context context) {
		return this.value();
	}

}
