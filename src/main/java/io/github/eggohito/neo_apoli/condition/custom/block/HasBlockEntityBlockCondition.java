package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;

public enum HasBlockEntityBlockCondition implements BlockCondition {

	INSTANCE;

	public static final MapCodec<HasBlockEntityBlockCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, HasBlockEntityBlockCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.HAS_BLOCK_ENTITY;
	}

	@Override
	public boolean test(Context context) {
		return context.hasParameter(NeoApoliContextParams.BLOCK_ENTITY)
			|| context.getOptional(NeoApoliContextParams.BLOCK_STATE).map(BlockState::hasBlockEntity).orElse(false);
	}

}
