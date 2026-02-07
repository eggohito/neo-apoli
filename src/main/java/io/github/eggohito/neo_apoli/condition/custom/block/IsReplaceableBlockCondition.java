package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public enum IsReplaceableBlockCondition implements BlockCondition {

	INSTANCE;

	public static final MapCodec<IsReplaceableBlockCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsReplaceableBlockCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.IS_REPLACEABLE;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.BLOCK_STATE)
			.map(BlockBehaviour.BlockStateBase::canBeReplaced)
			.orElse(false);
	}

}
