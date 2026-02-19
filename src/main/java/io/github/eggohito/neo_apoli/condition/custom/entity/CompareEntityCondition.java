package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareEntityCondition(Comparison comparison) implements EntityCondition, CompareMetaCondition {

	public static final MapCodec<CompareEntityCondition> MAP_CODEC = CompareMetaCondition.mapCodec(CompareEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareEntityCondition> STREAM_CODEC = CompareMetaCondition.streamCodec(CompareEntityCondition::new);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.COMPARE;
	}

}
