package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface EffectCondition extends Condition {

	Codec<EffectCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(EffectConditionType.CODEC.dispatch(EffectCondition::getType, EffectConditionType::mapCodec), ConstantEffectCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, EffectCondition> STREAM_CODEC = EffectConditionType.STREAM_CODEC.dispatch(EffectCondition::getType, EffectConditionType::streamCodec);

	@Override
	EffectConditionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.EFFECT_INSTANCE);
	}

}
