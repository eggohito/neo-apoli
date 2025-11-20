package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface EntityCondition extends Condition {

	Codec<EntityCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(EntityConditionType.CODEC.dispatch(EntityCondition::getType, EntityConditionType::mapCodec), ConstantEntityCondition.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, EntityCondition> PACKET_CODEC = EntityConditionType.PACKET_CODEC.dispatch(EntityCondition::getType, EntityConditionType::packetCodec);

	@Override
	EntityConditionType<?> getType();

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.THIS_ENTITY);
	}

	@Override
	default String asDisplayString() {
		return "Entity condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, this.getType()) + "\"";
	}

}
