package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.CompareMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CompareKeyCondition(Comparison comparison) implements KeyCondition, CompareMetaCondition {

	public static final MapCodec<CompareKeyCondition> CODEC = CompareMetaCondition.createCodec(CompareKeyCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, CompareKeyCondition> STREAM_CODEC = CompareMetaCondition.createStreamCodec(CompareKeyCondition::new);

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.COMPARE;
	}

	@Override
	public String asDisplayString() {
		return KeyCondition.super.asDisplayString();
	}

}
