package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum HasCollidedHorizontallyEntityCondition implements EntityCondition {

	INSTANCE;

	public static final MapCodec<HasCollidedHorizontallyEntityCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, HasCollidedHorizontallyEntityCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.HAS_COLLIDED_HORIZONTALLY;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.THIS_ENTITY)
			.map(entity -> entity.horizontalCollision)
			.orElse(false);
	}

}
