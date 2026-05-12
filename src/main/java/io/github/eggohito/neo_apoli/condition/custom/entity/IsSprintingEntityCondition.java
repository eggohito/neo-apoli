package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public enum IsSprintingEntityCondition implements EntityCondition {

	INSTANCE;

	public static final MapCodec<IsSprintingEntityCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsSprintingEntityCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.IS_SPRINTING;
	}

	@Override
	public boolean test(Context context) {

		try {
			return context.getOptional(NeoApoliContextParams.THIS_ENTITY)
				.stream()
				.filter(entity -> context.visitor().push(this))
				.anyMatch(Entity::isSprinting);
		}

		finally {
			context.visitor().pop(this);
		}

	}

}
