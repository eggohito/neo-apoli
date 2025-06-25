package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ConstantBiEntityCondition extends BiEntityCondition implements ConstantMetaCondition {

	public static final MapCodec<ConstantBiEntityCondition> CODEC = ConstantMetaCondition.codec(ConstantBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, ConstantBiEntityCondition> PACKET_CODEC = ConstantMetaCondition.packetCodec(ConstantBiEntityCondition::new).cast();

	private final boolean value;

	public ConstantBiEntityCondition(boolean value) {
		this.value = value;
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.CONSTANT;
	}

	@Override
	protected boolean impl(Context context) {
		return value();
	}

}
