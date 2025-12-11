package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface BiEntityCondition extends Condition {

	Codec<BiEntityCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(BiEntityConditionType.CODEC.dispatch(BiEntityCondition::getType, BiEntityConditionType::mapCodec), ConstantBiEntityCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, BiEntityCondition> STREAM_CODEC = BiEntityConditionType.STREAM_CODEC.dispatch(BiEntityCondition::getType, BiEntityConditionType::streamCodec);

	@Override
	BiEntityConditionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return NeoApoliContextKeySets.BIENTITY.allowed();
	}

	@Override
	default String asDisplayString() {
		return "Bi-entity condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, this.getType()) + "\"";
	}

}
