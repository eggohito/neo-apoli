package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
@Data
public final class EqualsBiEntityCondition extends BiEntityCondition {

	public static final MapCodec<EqualsBiEntityCondition> CODEC = MapCodec.unit(EqualsBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, EqualsBiEntityCondition> PACKET_CODEC = PacketCodec.unit(new EqualsBiEntityCondition());

	public EqualsBiEntityCondition() {

	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.EQUALS;
	}

	@Override
	protected boolean impl(Context context) {
		return Objects.equals(context.required(ContextParameters.ACTOR), context.required(ContextParameters.TARGET));
	}

}
