package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedEntityCondition(EntityCondition condition) implements EntityCondition, InvertedMetaCondition<EntityCondition> {

	public static final MapCodec<InvertedEntityCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.mapCodec(EntityCondition.CODEC, InvertedEntityCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.streamCodec(EntityCondition.STREAM_CODEC, InvertedEntityCondition::new));

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.INVERTED;
	}

}
