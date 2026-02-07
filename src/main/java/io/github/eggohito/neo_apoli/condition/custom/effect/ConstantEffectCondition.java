package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantEffectCondition(boolean value) implements EffectCondition, IConstantMetaCondition {

	public static final Codec<ConstantEffectCondition> INLINE_CODEC = IConstantMetaCondition.createInlineCodec(ConstantEffectCondition::new);

	public static final MapCodec<ConstantEffectCondition> MAP_CODEC = IConstantMetaCondition.mapCodec(ConstantEffectCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantEffectCondition> STREAM_CODEC = IConstantMetaCondition.streamCodec(ConstantEffectCondition::new);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.CONSTANT;
	}

}
