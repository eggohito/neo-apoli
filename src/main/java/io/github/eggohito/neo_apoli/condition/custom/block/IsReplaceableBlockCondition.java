package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.AbstractBlock;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record IsReplaceableBlockCondition() implements BlockCondition {

	public static final MapCodec<IsReplaceableBlockCondition> CODEC = MapCodec.unit(IsReplaceableBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsReplaceableBlockCondition> PACKET_CODEC = PacketCodecUtil.unit(IsReplaceableBlockCondition::new);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.IS_REPLACEABLE;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(ContextParameters.BLOCK_STATE)
			.map(AbstractBlock.AbstractBlockState::isReplaceable)
			.orElse(false);
	}

}
