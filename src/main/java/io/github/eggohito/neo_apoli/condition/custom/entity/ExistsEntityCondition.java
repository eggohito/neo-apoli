package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum ExistsEntityCondition implements EntityCondition {

	INSTANCE;

	public static final MapCodec<ExistsEntityCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, ExistsEntityCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.EXISTS;
	}

	@Override
	public boolean test(Context context) {
		return context.hasParameter(NeoApoliContextParams.THIS_ENTITY);
	}

}
