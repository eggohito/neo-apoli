package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface EntityCondition extends Condition {

	Codec<EntityCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(EntityConditionType.CODEC.dispatch(EntityCondition::getType, EntityConditionType::mapCodec), ConstantEntityCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, EntityCondition> STREAM_CODEC = EntityConditionType.STREAM_CODEC.dispatch(EntityCondition::getType, EntityConditionType::streamCodec);

	@Override
	EntityConditionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.THIS_ENTITY);
	}

}
