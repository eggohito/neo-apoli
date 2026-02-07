package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IAllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AllOfBlockCondition(List<BlockCondition> conditions) implements BlockCondition, IAllOfMetaCondition<BlockCondition> {

	public static final MapCodec<AllOfBlockCondition> MAP_CODEC = MapCodecUtil.lazy(AllOfBlockCondition.class.getSimpleName(), () -> IAllOfMetaCondition.mapCodec(BlockCondition.CODEC, AllOfBlockCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AllOfBlockCondition> STREAM_CODEC = StreamCodecUtil.lazy(AllOfBlockCondition.class.getSimpleName(), () -> IAllOfMetaCondition.streamCodec(BlockCondition.STREAM_CODEC, AllOfBlockCondition::new));

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.ALL_OF;
	}

}
