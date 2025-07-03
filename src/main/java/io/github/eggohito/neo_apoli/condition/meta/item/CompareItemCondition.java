package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class CompareItemCondition extends ItemCondition implements CompareMetaCondition {

	public static final MapCodec<CompareItemCondition> CODEC = CompareMetaCondition.codec(CompareItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareItemCondition> PACKET_CODEC = CompareMetaCondition.packetCodec(CompareItemCondition::new);

	private final Comparison comparison;

	public CompareItemCondition(Comparison comparison) {
		this.comparison = comparison;
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.COMPARE;
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
