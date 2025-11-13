package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.meta.MetaConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record CompareCondition(Comparison comparison) implements CompareMetaCondition {

	public static final MapCodec<CompareCondition> CODEC = CompareMetaCondition.codec(CompareCondition::new);
	public static final PacketCodec<RegistryByteBuf, CompareCondition> PACKET_CODEC = CompareMetaCondition.packetCodec(CompareCondition::new);

	@Override
	public ConditionType<?> getType() {
		return MetaConditionTypes.COMPARE;
	}

}
