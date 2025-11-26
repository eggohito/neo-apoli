package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedCondition(Condition condition) implements InvertedMetaCondition<Condition> {

	public static final MapCodec<InvertedCondition> CODEC = MapCodecUtil.lazy(InvertedCondition.class.getSimpleName(), () -> InvertedMetaCondition.createCodec(Condition.CODEC, InvertedCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedCondition.class.getSimpleName(), () -> InvertedMetaCondition.createStreamCodec(Condition.STREAM_CODEC, InvertedCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.INVERTED;
	}

}
