package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public abstract class BlockCondition extends Condition {

	public static final MapCodec<BlockCondition> MAP_CODEC = BlockConditionType.CODEC.dispatchMap("type", BlockCondition::getType, BlockConditionType::mapCodec);
	public static final Codec<BlockCondition> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, BlockCondition> PACKET_CODEC = BlockConditionType.PACKET_CODEC.dispatch(BlockCondition::getType, BlockConditionType::packetCodec);

	@Override
	public abstract BlockConditionType<?> getType();

	@Override
	public boolean test(Context context) {

		context = new ContextImpl.Builder(context)
			.add(ContextParameters.POSITION, context.required(ContextParameters.BLOCK_POS).toCenterPos())
			.build(context.getWorld());

		return super.test(context);

	}

	@Override
	public ConditionCategory<BlockCondition> getCategory() {
		return ConditionCategories.BLOCK_CONDITION;
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return ContextTypes.BLOCK.getAllowed();
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.BLOCK_CONDITION_TYPE, this.getType()) + "\"";
	}

	@Deprecated(forRemoval = true)
	protected BlockPos getBlockPos(Context context) {
		return BlockPos.ofFloored(context.required(ContextParameters.ENTITY_POS));
	}

}
