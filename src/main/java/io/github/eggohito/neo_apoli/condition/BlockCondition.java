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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class BlockCondition extends Condition {

	public static final Codec<BlockCondition> CODEC = BlockConditionTypes.CODEC.dispatch("type", BlockCondition::getType, BlockConditionType::mapCodec);
	public static final PacketCodec<RegistryByteBuf, BlockCondition> PACKET_CODEC = BlockConditionTypes.PACKET_CODEC.dispatch(BlockCondition::getType, BlockConditionType::packetCodec);

	@Override
	public abstract BlockConditionType<?> getType();

	@Override
	public ConditionCategory<BlockCondition> getCategory() {
		return ConditionCategories.BLOCK_CONDITION;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.BLOCK_CONDITION_TYPE, this.getType()) + "\"";
	}

	protected BlockPos getBlockPos(Context context) {
		return BlockPos.ofFloored(context.required(ContextParameters.POSITION));
	}

	protected BlockState getBlockState(Context context) {
		return context.optional(ContextParameters.BLOCK_STATE).orElseGet(() -> context.getWorld().getBlockState(this.getBlockPos(context)));
	}

	@Nullable
	protected BlockEntity getBlockEntity(Context context) {
		return context.optional(ContextParameters.BLOCK_ENTITY).orElseGet(() -> context.getWorld().getBlockEntity(this.getBlockPos(context)));
	}

}
