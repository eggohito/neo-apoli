package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class InvertedBlockCondition extends BlockCondition implements InvertedMetaCondition<BlockCondition> {

	public static final MapCodec<InvertedBlockCondition> CODEC = MapCodecUtil.lazy(InvertedBlockCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(BlockCondition.CODEC, InvertedBlockCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedBlockCondition> PACKET_CODEC = PacketCodecUtil.lazy(InvertedBlockCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(BlockCondition.PACKET_CODEC, InvertedBlockCondition::new));

	private final BlockCondition condition;

	public InvertedBlockCondition(BlockCondition condition) {
		this.condition = condition;
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.INVERTED;
	}

	@Override
	public boolean impl(Context context) {
		return InvertedMetaCondition.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		InvertedMetaCondition.super.validate(reporter);
	}

}
