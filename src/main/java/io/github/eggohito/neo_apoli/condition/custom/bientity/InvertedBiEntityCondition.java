package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IInvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedBiEntityCondition(BiEntityCondition condition) implements BiEntityCondition, IInvertedMetaCondition<BiEntityCondition> {

	public static final MapCodec<InvertedBiEntityCondition> CODEC = MapCodecUtil.lazy(InvertedBiEntityCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createCodec(BiEntityCondition.CODEC, InvertedBiEntityCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedBiEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedBiEntityCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createStreamCodec(BiEntityCondition.STREAM_CODEC, InvertedBiEntityCondition::new));

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return BiEntityCondition.super.asDisplayString();
	}

}
