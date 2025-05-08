package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface BlockCondition extends Condition<BlockConditionType<?>> {

	Codec<BlockCondition> CODEC = BlockConditionTypes.CODEC.dispatch(TYPE_KEY, BlockCondition::getType, BlockConditionType::mapCodec);
	PacketCodec<RegistryByteBuf, BlockCondition> PACKET_CODEC = BlockConditionTypes.PACKET_CODEC.dispatch(BlockCondition::getType, BlockConditionType::packetCodec);

	@Override
	default String asDisplayString() {
		return "Block condition (with type \"" + RegistryUtil.getId(NeoApoliRegistries.BLOCK_CONDITION_TYPE, this.getType()) + "\")";
	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

}
