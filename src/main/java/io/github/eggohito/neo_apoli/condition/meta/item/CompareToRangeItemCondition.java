package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareToRangeMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class CompareToRangeItemCondition extends ItemCondition implements CompareToRangeMetaCondition {

	public static final MapCodec<CompareToRangeItemCondition> CODEC = CompareToRangeMetaCondition.codec(CompareToRangeItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareToRangeItemCondition> PACKET_CODEC = CompareToRangeMetaCondition.packetCodec(CompareToRangeItemCondition::new);

	private final NumberProvider value;

	private final Optional<NumberProvider> min;
	private final Optional<NumberProvider> max;

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.COMPARE_TO_RANGE;
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
