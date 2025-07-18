package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class CompareToRangeBlockCondition extends BlockCondition implements CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeBlockCondition> CODEC = CompareToRangeMetaCondition.codec(CompareToRangeBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareToRangeBlockCondition> PACKET_CODEC = CompareToRangeMetaCondition.packetCodec(CompareToRangeBlockCondition::new);

	private final NumberProvider value;

	private final Optional<NumberProvider> min;
	private final Optional<NumberProvider> max;

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.COMPARE_TO_RANGE;
	}

	@Override
	protected boolean impl(Context context) {
		return CompareToRangeMetaCondition.super.internalImpl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		CompareToRangeMetaCondition.super.validate(reporter);
	}

}
