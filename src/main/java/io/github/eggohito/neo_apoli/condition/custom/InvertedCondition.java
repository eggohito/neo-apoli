package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedCondition(Condition condition) implements InvertedMetaCondition<Condition> {

	public static final MapCodec<InvertedCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedCondition.class.getSimpleName(), () -> InvertedMetaCondition.mapCodec(Condition.CODEC, InvertedCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedCondition.class.getSimpleName(), () -> InvertedMetaCondition.streamCodec(Condition.STREAM_CODEC, InvertedCondition::new));

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.INVERTED;
	}

}
