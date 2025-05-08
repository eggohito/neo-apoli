package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ConstantBlockCondition(boolean value) implements BlockCondition, ConstantMetaCondition<BlockConditionType<?>> {

	public static final MapCodec<ConstantBlockCondition> CODEC = ConstantMetaCondition.createCodec(ConstantBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, ConstantBlockCondition> PACKET_CODEC = ConstantMetaCondition.createPacketCodec(ConstantBlockCondition::new).cast();

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.CONSTANT;
	}

}
