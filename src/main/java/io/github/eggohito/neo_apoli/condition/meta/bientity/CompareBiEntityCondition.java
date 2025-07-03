package io.github.eggohito.neo_apoli.condition.meta.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class CompareBiEntityCondition extends BiEntityCondition implements CompareMetaCondition {

	public static final MapCodec<CompareBiEntityCondition> CODEC = CompareMetaCondition.codec(CompareBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareBiEntityCondition> PACKET_CODEC = CompareMetaCondition.packetCodec(CompareBiEntityCondition::new);

	private final Comparison comparison;

	public CompareBiEntityCondition(Comparison comparison) {
		this.comparison = comparison;
	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.COMPARE;
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
