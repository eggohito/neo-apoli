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

public record IsSneakingEntityCondition() implements EntityCondition {

	public static final MapCodec<IsSneakingEntityCondition> CODEC = MapCodec.unit(IsSneakingEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsSneakingEntityCondition> STREAM_CODEC = StreamCodecUtil.unit(IsSneakingEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_SNEAKING;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.THIS_ENTITY)
			.stream()
			.anyMatch(Entity::isShiftKeyDown);
	}

}
