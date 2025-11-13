package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface BlockCondition extends Condition {

	Codec<BlockCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(BlockConditionType.CODEC.dispatch(BlockCondition::getType, BlockConditionType::mapCodec), ConstantBlockCondition.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, BlockCondition> PACKET_CODEC = BlockConditionType.PACKET_CODEC.dispatch(BlockCondition::getType, BlockConditionType::packetCodec);

	@Override
	BlockConditionType<?> getType();

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return ContextTypes.BLOCK.getRequired();
	}

	@Override
	default String asDisplayString() {
		return "Block condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, this.getType()) + "\"";
	}

}
