package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record EqualsBiEntityCondition() implements BiEntityCondition {

	public static final MapCodec<EqualsBiEntityCondition> CODEC = MapCodec.unit(EqualsBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, EqualsBiEntityCondition> STREAM_CODEC = StreamCodecUtil.unit(EqualsBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.EQUALS;
	}

	@Override
	public boolean test(Context context) {

		Entity actor = context.nullable(NeoApoliContextKeys.ACTOR);
		Entity target = context.nullable(NeoApoliContextKeys.TARGET);

		return actor != null
			&& actor.equals(target);

	}

}
