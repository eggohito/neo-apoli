package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedBlockCondition(BlockCondition condition) implements BlockCondition, InvertedMetaCondition<BlockCondition, BlockConditionType<?>> {

	public static final MapCodec<InvertedBlockCondition> CODEC = NeoApoliMapCodecs.lazy(InvertedBlockCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(BlockCondition.CODEC, InvertedBlockCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedBlockCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy(InvertedBlockCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(BlockCondition.PACKET_CODEC, InvertedBlockCondition::new));

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.INVERTED;
	}

}
