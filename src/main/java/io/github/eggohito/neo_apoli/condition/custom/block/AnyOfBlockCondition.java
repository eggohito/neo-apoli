package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfBlockCondition(List<BlockCondition> conditions) implements BlockCondition, IAnyOfMetaCondition<BlockCondition> {

	public static final MapCodec<AnyOfBlockCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfBlockCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.mapCodec(BlockCondition.CODEC, AnyOfBlockCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfBlockCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfBlockCondition.class.getSimpleName(), () -> IAnyOfMetaCondition.streamCodec(BlockCondition.STREAM_CODEC, AnyOfBlockCondition::new));

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.ANY_OF;
	}

}
