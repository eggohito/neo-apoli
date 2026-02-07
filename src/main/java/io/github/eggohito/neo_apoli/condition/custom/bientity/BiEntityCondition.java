package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
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
		return Set.of(NeoApoliContextParams.ACTOR_ENTITY, NeoApoliContextParams.TARGET_ENTITY);
	}

}
