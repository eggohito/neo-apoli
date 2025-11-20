package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface KeyCondition extends Condition {

	Codec<KeyCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(KeyConditionType.CODEC.dispatch(KeyCondition::getType, KeyConditionType::mapCodec), ConstantKeyCondition.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, KeyCondition> PACKET_CODEC = KeyConditionType.PACKET_CODEC.dispatch(KeyCondition::getType, KeyConditionType::packetCodec);

	@Override
	KeyConditionType<?> getType();

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.THIS_ENTITY);
	}

	@Override
	default String asDisplayString() {
		return "Key condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, this.getType()) + "\"";
	}

}
