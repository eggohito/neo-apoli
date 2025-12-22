package io.github.eggohito.neo_apoli.condition.custom.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedMetaCondition(Condition condition) implements IInvertedMetaCondition<Condition> {

	public static final MapCodec<InvertedMetaCondition> CODEC = MapCodecUtil.lazy(InvertedMetaCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createCodec(Condition.CODEC, InvertedMetaCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedMetaCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedMetaCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createStreamCodec(Condition.STREAM_CODEC, InvertedMetaCondition::new));

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.INVERTED;
	}

}
