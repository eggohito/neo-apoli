package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEffectConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantEffectCondition(boolean value) implements EffectCondition, ConstantMetaCondition {

	public static final Codec<ConstantEffectCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantEffectCondition::new);

	public static final MapCodec<ConstantEffectCondition> MAP_CODEC = ConstantMetaCondition.mapCodec(ConstantEffectCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantEffectCondition> STREAM_CODEC = ConstantMetaCondition.streamCodec(ConstantEffectCondition::new);

	@Override
	public EffectCondition.Type<?> getType() {
		return NeoApoliEffectConditionTypes.CONSTANT;
	}

}
