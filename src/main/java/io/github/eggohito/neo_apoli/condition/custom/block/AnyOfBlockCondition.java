package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AnyOfBlockCondition(List<BlockCondition> conditions) implements BlockCondition, AnyOfMetaCondition<BlockCondition> {

	public static final MapCodec<AnyOfBlockCondition> CODEC = MapCodecUtil.lazy(AnyOfBlockCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(BlockCondition.CODEC, AnyOfBlockCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfBlockCondition> PACKET_CODEC = PacketCodecUtil.lazy(AnyOfBlockCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(BlockCondition.PACKET_CODEC, AnyOfBlockCondition::new));

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.ANY_OF;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}
