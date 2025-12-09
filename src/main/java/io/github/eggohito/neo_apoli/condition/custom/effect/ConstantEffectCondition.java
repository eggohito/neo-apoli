package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantEffectCondition(boolean value) implements EffectCondition, ConstantMetaCondition {

	public static final Codec<ConstantEffectCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantEffectCondition::new);

	public static final MapCodec<ConstantEffectCondition> CODEC = ConstantMetaCondition.createCodec(ConstantEffectCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantEffectCondition> STREAM_CODEC = ConstantMetaCondition.createStreamCodec(ConstantEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.CONSTANT;
	}

	@Override
	public String asDisplayString() {
		return EffectCondition.super.asDisplayString();
	}

}
