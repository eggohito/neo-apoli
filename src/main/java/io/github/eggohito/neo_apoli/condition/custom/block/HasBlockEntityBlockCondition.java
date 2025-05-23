package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record HasBlockEntityBlockCondition() implements BlockCondition {

	public static final MapCodec<HasBlockEntityBlockCondition> CODEC = MapCodec.unit(HasBlockEntityBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, HasBlockEntityBlockCondition> PACKET_CODEC = PacketCodec.unit(new HasBlockEntityBlockCondition());

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.HAS_BLOCK_ENTITY;
	}

	@Override
	public boolean test(Context context) {
		return this.getBlockState(context).hasBlockEntity();
	}

}
