package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class CompareToRangeBiEntityCondition extends BiEntityCondition implements CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeBiEntityCondition> CODEC = CompareToRangeMetaCondition.codec(CompareToRangeBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareToRangeBiEntityCondition> PACKET_CODEC = CompareToRangeMetaCondition.packetCodec(CompareToRangeBiEntityCondition::new);

	private final NumberProvider value;

	private final Optional<NumberProvider> min;
	private final Optional<NumberProvider> max;

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.COMPARE_TO_RANGE;
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
