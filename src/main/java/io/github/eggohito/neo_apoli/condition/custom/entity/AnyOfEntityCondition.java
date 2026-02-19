package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfEntityCondition(List<EntityCondition> conditions) implements EntityCondition, AnyOfMetaCondition<EntityCondition> {

	public static final MapCodec<AnyOfEntityCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfEntityCondition.class.getSimpleName(), () -> AnyOfMetaCondition.mapCodec(EntityCondition.CODEC, AnyOfEntityCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfEntityCondition.class.getSimpleName(), () -> AnyOfMetaCondition.streamCodec(EntityCondition.STREAM_CODEC, AnyOfEntityCondition::new));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ANY_OF;
	}

}
