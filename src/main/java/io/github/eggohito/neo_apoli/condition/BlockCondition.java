package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public interface BlockCondition extends Condition<BlockConditionType<?>> {

	Codec<BlockCondition> CODEC = BlockConditionTypes.CODEC.dispatch(TYPE_KEY, BlockCondition::getType, BlockConditionType::mapCodec);
	PacketCodec<RegistryByteBuf, BlockCondition> PACKET_CODEC = BlockConditionTypes.PACKET_CODEC.dispatch(BlockCondition::getType, BlockConditionType::packetCodec);

	@Override
	default ConditionCategory<BlockCondition> getCategory() {
		return ConditionCategories.BLOCK_CONDITION;
	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

	@Override
	default String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.BLOCK_CONDITION_TYPE, this.getType()) + "\"";
	}

	default BlockState getBlockState(Context context) {
		BlockPos blockPos = BlockPos.ofFloored(context.required(ContextParameters.POSITION));
		return context.optional(ContextParameters.BLOCK_STATE).orElseGet(() -> context.getWorld().getBlockState(blockPos));
	}

}
