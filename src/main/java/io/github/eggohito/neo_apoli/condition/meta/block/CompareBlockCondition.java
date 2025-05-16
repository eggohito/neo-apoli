package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record CompareBlockCondition(Comparison comparison) implements BlockCondition, CompareMetaCondition<BlockConditionType<?>> {

	public static final MapCodec<CompareBlockCondition> CODEC = CompareMetaCondition.codec(CompareBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareBlockCondition> PACKET_CODEC = CompareMetaCondition.packetCodec(CompareBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.COMPARE;
	}

}
