package io.github.eggohito.neo_apoli.condition.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class CompareToRangeEntityCondition extends EntityCondition implements CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeEntityCondition> CODEC = CompareToRangeMetaCondition.codec(CompareToRangeEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareToRangeEntityCondition> PACKET_CODEC = CompareToRangeMetaCondition.packetCodec(CompareToRangeEntityCondition::new);

	private final NumberProvider value;

	private final Optional<NumberProvider> min;
	private final Optional<NumberProvider> max;

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.COMPARE_TO_RANGE;
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
