package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class CompareEntityCondition extends EntityCondition implements CompareMetaCondition {

	public static final MapCodec<CompareEntityCondition> CODEC = CompareMetaCondition.codec(CompareEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareEntityCondition> PACKET_CODEC = CompareMetaCondition.packetCodec(CompareEntityCondition::new);

	private final Comparison comparison;

	public CompareEntityCondition(Comparison comparison) {
		this.comparison = comparison;
	}

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.COMPARE;
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
