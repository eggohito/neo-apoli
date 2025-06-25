package io.github.eggohito.neo_apoli.condition.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class CompareBlockCondition extends BlockCondition implements CompareMetaCondition {

	public static final MapCodec<CompareBlockCondition> CODEC = CompareMetaCondition.codec(CompareBlockCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareBlockCondition> PACKET_CODEC = CompareMetaCondition.packetCodec(CompareBlockCondition::new);

	private final Comparison comparison;

	public CompareBlockCondition(Comparison comparison) {
		this.comparison = comparison;
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.COMPARE;
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
