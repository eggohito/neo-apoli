package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record HasBlockEntityBlockCondition() implements BlockCondition {

	public static final MapCodec<HasBlockEntityBlockCondition> CODEC = MapCodec.unit(HasBlockEntityBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, HasBlockEntityBlockCondition> PACKET_CODEC = PacketCodec.unit(new HasBlockEntityBlockCondition());

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.HAS_BLOCK_ENTITY;
	}

	@Override
	public boolean test(Context context) {

		World world = context.getWorld();
		BlockPos pos = BlockPos.ofFloored(context.requiredParameter(ContextParameters.POSITION));

		return context.optionalParameter(ContextParameters.BLOCK_STATE)
			.orElseGet(() -> world.getBlockState(pos))
			.hasBlockEntity();

	}

}
