package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;

public record HasBlockEntityBlockCondition() implements BlockCondition {

	public static final MapCodec<HasBlockEntityBlockCondition> CODEC = MapCodec.unit(HasBlockEntityBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, HasBlockEntityBlockCondition> STREAM_CODEC = StreamCodecUtil.unit(HasBlockEntityBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.HAS_BLOCK_ENTITY;
	}

	@Override
	public boolean test(Context context) {
		return context.hasParameter(NeoApoliContextKeys.BLOCK_ENTITY)
			|| context.optional(NeoApoliContextKeys.BLOCK_STATE).map(BlockState::hasBlockEntity).orElse(false);
	}

}
