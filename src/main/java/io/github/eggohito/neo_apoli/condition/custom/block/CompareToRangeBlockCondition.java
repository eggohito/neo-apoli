package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

public record CompareToRangeBlockCondition(NumberProvider value, Optional<NumberProvider> min, Optional<NumberProvider> max) implements BlockCondition, CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeBlockCondition> CODEC = CompareToRangeMetaCondition.codec(CompareToRangeBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareToRangeBlockCondition> PACKET_CODEC = CompareToRangeMetaCondition.packetCodec(CompareToRangeBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	public String asDisplayString() {
		return BlockCondition.super.asDisplayString();
	}

}
