package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.meta.MultiMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public record AnyOfBlockCondition(List<BlockCondition> conditions) implements BlockCondition, AnyOfMetaCondition<BlockCondition, BlockConditionType<?>> {

	public static final MapCodec<AnyOfBlockCondition> CODEC = NeoApoliCodecs.lazyMap("AnyOfBlockCondition", () -> MultiMetaCondition.createCodec(BlockCondition.CODEC, AnyOfBlockCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfBlockCondition> PACKET_CODEC = NeoApoliPacketCodecs.lazy("AnyOfBlockCondition", () -> MultiMetaCondition.createPacketCodec(BlockCondition.PACKET_CODEC, AnyOfBlockCondition::new));

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.ANY_OF;
	}

}
