package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record EqualsBiEntityCondition() implements BiEntityCondition {

	public static final MapCodec<EqualsBiEntityCondition> CODEC = MapCodec.unit(EqualsBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, EqualsBiEntityCondition> PACKET_CODEC = PacketCodecUtil.unit(EqualsBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.EQUALS;
	}

	@Override
	public boolean test(Context context) {

		Entity actor = context.nullable(ContextParameters.ACTOR);
		Entity target = context.nullable(ContextParameters.TARGET);

		return actor != null
			&& actor.equals(target);

	}

}
