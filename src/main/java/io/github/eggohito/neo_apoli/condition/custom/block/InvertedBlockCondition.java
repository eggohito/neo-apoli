package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record InvertedBlockCondition(BlockCondition condition) implements BlockCondition, InvertedMetaCondition<BlockCondition> {

	public static final MapCodec<InvertedBlockCondition> CODEC = MapCodecUtil.lazy(InvertedBlockCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(BlockCondition.CODEC, InvertedBlockCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedBlockCondition> PACKET_CODEC = PacketCodecUtil.lazy(InvertedBlockCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(BlockCondition.PACKET_CODEC, InvertedBlockCondition::new));

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}
