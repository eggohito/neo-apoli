package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareDamageCondition(Comparison comparison) implements DamageCondition, CompareMetaCondition {

	public static final MapCodec<CompareDamageCondition> CODEC = CompareMetaCondition.createCodec(CompareDamageCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareDamageCondition> STREAM_CODEC = CompareMetaCondition.createStreamCodec(CompareDamageCondition::new);

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.COMPARE;
	}

	@Override
	public String asDisplayString() {
		return DamageCondition.super.asDisplayString();
	}

}
