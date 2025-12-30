package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ExistsEntityCondition() implements EntityCondition {

	public static final MapCodec<ExistsEntityCondition> CODEC = MapCodec.unit(ExistsEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExistsEntityCondition> STREAM_CODEC = StreamCodecUtil.unit(ExistsEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.EXISTS;
	}

	@Override
	public boolean test(Context context) {
		return context.hasParameter(NeoApoliContextKeys.THIS_ENTITY);
	}

}
