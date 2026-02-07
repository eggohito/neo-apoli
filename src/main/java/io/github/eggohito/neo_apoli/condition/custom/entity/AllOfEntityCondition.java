package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfEntityCondition(List<EntityCondition> conditions) implements EntityCondition, IAllOfMetaCondition<EntityCondition> {

	public static final MapCodec<AllOfEntityCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfEntityCondition.class.getSimpleName(), () -> IAllOfMetaCondition.mapCodec(EntityCondition.CODEC, AllOfEntityCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfEntityCondition.class.getSimpleName(), () -> IAllOfMetaCondition.streamCodec(EntityCondition.STREAM_CODEC, AllOfEntityCondition::new));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.ALL_OF;
	}

}
