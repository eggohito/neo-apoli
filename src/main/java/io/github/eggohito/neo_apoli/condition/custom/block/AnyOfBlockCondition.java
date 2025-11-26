package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfBlockCondition(List<BlockCondition> conditions) implements BlockCondition, AnyOfMetaCondition<BlockCondition> {

	public static final MapCodec<AnyOfBlockCondition> CODEC = MapCodecUtil.lazy(AnyOfBlockCondition.class.getSimpleName(), () -> AnyOfMetaCondition.createCodec(BlockCondition.CODEC, AnyOfBlockCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfBlockCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfBlockCondition.class.getSimpleName(), () -> AnyOfMetaCondition.createStreamCodec(BlockCondition.STREAM_CODEC, AnyOfBlockCondition::new));

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.ANY_OF;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}
