package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface BlockCondition extends Condition {

	Codec<BlockCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(BlockConditionType.CODEC.dispatch(BlockCondition::getType, BlockConditionType::mapCodec), ConstantBlockCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, BlockCondition> STREAM_CODEC = BlockConditionType.STREAM_CODEC.dispatch(BlockCondition::getType, BlockConditionType::streamCodec);

	@Override
	BlockConditionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.BLOCK_POS, NeoApoliContextParams.BLOCK_STATE);
	}

}
