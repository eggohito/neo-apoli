package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public record IsReplaceableBlockCondition() implements BlockCondition {

	public static final MapCodec<IsReplaceableBlockCondition> CODEC = MapCodec.unit(IsReplaceableBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsReplaceableBlockCondition> STREAM_CODEC = StreamCodecUtil.unit(IsReplaceableBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.IS_REPLACEABLE;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.BLOCK_STATE)
			.map(BlockBehaviour.BlockStateBase::canBeReplaced)
			.orElse(false);
	}

}
