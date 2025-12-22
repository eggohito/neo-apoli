package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IInvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedBlockCondition(BlockCondition condition) implements BlockCondition, IInvertedMetaCondition<BlockCondition> {

	public static final MapCodec<InvertedBlockCondition> CODEC = MapCodecUtil.lazy(InvertedBlockCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createCodec(BlockCondition.CODEC, InvertedBlockCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedBlockCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedBlockCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createStreamCodec(BlockCondition.STREAM_CODEC, InvertedBlockCondition::new));

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}
