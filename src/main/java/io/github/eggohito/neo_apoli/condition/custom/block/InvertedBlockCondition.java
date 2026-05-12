package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedBlockCondition(BlockCondition condition) implements BlockCondition, InvertedMetaCondition<BlockCondition> {

	public static final MapCodec<InvertedBlockCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedBlockCondition.class.getSimpleName(), () -> InvertedMetaCondition.mapCodec(BlockCondition.CODEC, InvertedBlockCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedBlockCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedBlockCondition.class.getSimpleName(), () -> InvertedMetaCondition.streamCodec(BlockCondition.STREAM_CODEC, InvertedBlockCondition::new));

	@Override
	public BlockCondition.Type<?> getType() {
		return NeoApoliBlockConditionTypes.INVERTED;
	}

}
