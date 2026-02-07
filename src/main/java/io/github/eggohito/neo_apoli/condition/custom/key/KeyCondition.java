package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface KeyCondition extends Condition {

	Codec<KeyCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(KeyConditionType.CODEC.dispatch(KeyCondition::getType, KeyConditionType::mapCodec), ConstantKeyCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, KeyCondition> STREAM_CODEC = KeyConditionType.STREAM_CODEC.dispatch(KeyCondition::getType, KeyConditionType::streamCodec);

	@Override
	KeyConditionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.THIS_ENTITY);
	}

}
