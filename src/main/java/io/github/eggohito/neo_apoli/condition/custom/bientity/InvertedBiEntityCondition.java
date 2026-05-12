package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedBiEntityCondition(BiEntityCondition condition) implements BiEntityCondition, InvertedMetaCondition<BiEntityCondition> {

	public static final MapCodec<InvertedBiEntityCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedBiEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.mapCodec(BiEntityCondition.CODEC, InvertedBiEntityCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedBiEntityCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedBiEntityCondition.class.getSimpleName(), () -> InvertedMetaCondition.streamCodec(BiEntityCondition.STREAM_CODEC, InvertedBiEntityCondition::new));

	@Override
	public BiEntityCondition.Type<?> getType() {
		return NeoApoliBiEntityConditionTypes.INVERTED;
	}

}
