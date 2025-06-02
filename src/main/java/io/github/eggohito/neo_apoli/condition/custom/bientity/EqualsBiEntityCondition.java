package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Objects;

public record EqualsBiEntityCondition() implements BiEntityCondition {

	public static final MapCodec<EqualsBiEntityCondition> CODEC = MapCodec.unit(EqualsBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, EqualsBiEntityCondition> PACKET_CODEC = PacketCodec.unit(new EqualsBiEntityCondition());

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.EQUALS;
	}

	@Override
	public boolean test(Context context) {
		return Objects.equals(context.required(ContextParameters.ACTOR), context.required(ContextParameters.TARGET));
	}

}
