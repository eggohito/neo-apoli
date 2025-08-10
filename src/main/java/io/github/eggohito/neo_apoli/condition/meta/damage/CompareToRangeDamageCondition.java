package io.github.eggohito.neo_apoli.condition.meta.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class CompareToRangeDamageCondition extends DamageCondition implements CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeDamageCondition> CODEC = CompareToRangeMetaCondition.codec(CompareToRangeDamageCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareToRangeDamageCondition> PACKET_CODEC = CompareToRangeMetaCondition.packetCodec(CompareToRangeDamageCondition::new);

	private final NumberProvider value;
	private final Optional<NumberProvider> min;
	private final Optional<NumberProvider> max;

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.COMPARE_TO_RANGE;
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
