package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AllOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AllOfBlockCondition(List<BlockCondition> conditions) implements BlockCondition, AllOfMetaCondition<BlockCondition> {

	public static final MapCodec<AllOfBlockCondition> CODEC = MapCodecUtil.lazy(AllOfBlockCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(BlockCondition.CODEC, AllOfBlockCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfBlockCondition> PACKET_CODEC = PacketCodecUtil.lazy(AllOfBlockCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(BlockCondition.PACKET_CODEC, AllOfBlockCondition::new));

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.ALL_OF;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}
