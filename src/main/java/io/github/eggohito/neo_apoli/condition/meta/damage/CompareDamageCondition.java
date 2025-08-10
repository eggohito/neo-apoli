package io.github.eggohito.neo_apoli.condition.meta.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class CompareDamageCondition extends DamageCondition implements CompareMetaCondition {

	public static final MapCodec<CompareDamageCondition> CODEC = CompareMetaCondition.codec(CompareDamageCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareDamageCondition> PACKET_CODEC = CompareMetaCondition.packetCodec(CompareDamageCondition::new);

	private final Comparison comparison;

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.COMPARE;
	}

	@Override
	public boolean impl(Context context) {
		return CompareMetaCondition.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		CompareMetaCondition.super.validate(reporter);
	}

}
