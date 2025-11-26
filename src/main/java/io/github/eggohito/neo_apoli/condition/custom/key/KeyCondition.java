package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface KeyCondition extends Condition {

	Codec<KeyCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(KeyConditionType.CODEC.dispatch(KeyCondition::getType, KeyConditionType::mapCodec), ConstantKeyCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, KeyCondition> STREAM_CODEC = KeyConditionType.STREAM_CODEC.dispatch(KeyCondition::getType, KeyConditionType::packetCodec);

	@Override
	KeyConditionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.THIS_ENTITY);
	}

	@Override
	default String asDisplayString() {
		return "Key condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, this.getType()) + "\"";
	}

}
