package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBlockConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;

public enum HasBlockEntityBlockCondition implements BlockCondition {

	INSTANCE;

	public static final MapCodec<HasBlockEntityBlockCondition> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, HasBlockEntityBlockCondition> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public BlockCondition.Type<?> getType() {
		return NeoApoliBlockConditionTypes.HAS_BLOCK_ENTITY;
	}

	@Override
	public boolean test(Context context) {
		return context.hasParameter(NeoApoliContextParams.BLOCK_ENTITY)
			|| context.getOptional(NeoApoliContextParams.BLOCK_STATE).map(BlockState::hasBlockEntity).orElse(false);
	}

}
