package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.DynamicMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record DynamicEffectCondition(BooleanProvider value) implements EffectCondition, DynamicMetaCondition {

	public static final MapCodec<DynamicEffectCondition> CODEC = DynamicMetaCondition.createCodec(DynamicEffectCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicEffectCondition> STREAM_CODEC = DynamicMetaCondition.createStreamCodec(DynamicEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.DYNAMIC;
	}

	@Override
	public String asDisplayString() {
		return EffectCondition.super.asDisplayString();
	}

}
