package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record IsOnFireEntityCondition() implements EntityCondition {

	public static final MapCodec<IsOnFireEntityCondition> CODEC = MapCodec.unit(IsOnFireEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsOnFireEntityCondition> STREAM_CODEC = StreamCodecUtil.unit(IsOnFireEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_ON_FIRE;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.THIS_ENTITY)
			.map(Entity::isOnFire)
			.orElse(false);
	}

}
