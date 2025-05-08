package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.meta.MultiMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfBlockCondition(List<BlockCondition> conditions) implements BlockCondition, AllOfMetaCondition<BlockCondition, BlockConditionType<?>> {

	public static final MapCodec<AllOfBlockCondition> CODEC = MultiMetaCondition.createCodec(BlockCondition.CODEC, AllOfBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, AllOfBlockCondition> PACKET_CODEC = MultiMetaCondition.createPacketCodec(BlockCondition.PACKET_CODEC, AllOfBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.ALL_OF;
	}

}
