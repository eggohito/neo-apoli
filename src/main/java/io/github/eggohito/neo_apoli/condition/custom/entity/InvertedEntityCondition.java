package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IInvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedEntityCondition(EntityCondition condition) implements EntityCondition, IInvertedMetaCondition<EntityCondition> {

	public static final MapCodec<InvertedEntityCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedEntityCondition.class.getSimpleName(), () -> IInvertedMetaCondition.mapCodec(EntityCondition.CODEC, InvertedEntityCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedEntityCondition.class.getSimpleName(), () -> IInvertedMetaCondition.streamCodec(EntityCondition.STREAM_CODEC, InvertedEntityCondition::new));

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.INVERTED;
	}

}
