package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface DamageCondition extends Condition {

	Codec<DamageCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(DamageConditionType.CODEC.dispatch(DamageCondition::getType, DamageConditionType::mapCodec), ConstantDamageCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, DamageCondition> STREAM_CODEC = DamageConditionType.STREAM_CODEC.dispatch(DamageCondition::getType, DamageConditionType::packetCodec);

	@Override
	DamageConditionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return NeoApoliContextKeySets.DAMAGE.required();
	}

	@Override
	default String asDisplayString() {
		return "Damage condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, this.getType()) + "\"";
	}

}
