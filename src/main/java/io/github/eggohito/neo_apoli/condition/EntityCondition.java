package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface EntityCondition extends Condition<EntityConditionType<?>> {

	Codec<EntityCondition> CODEC = EntityConditionTypes.CODEC.dispatch(TYPE_KEY, EntityCondition::getType, EntityConditionType::mapCodec);
	PacketCodec<RegistryByteBuf, EntityCondition> PACKET_CODEC = EntityConditionTypes.PACKET_CODEC.dispatch(EntityCondition::getType, EntityConditionType::packetCodec);

	@Override
	default ConditionCategory<EntityCondition> getCategory() {
		return ConditionCategories.ENTITY_CONDITION;
	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.THIS_ENTITY, ContextParameters.POSITION);
	}

	@Override
	default String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.ENTITY_CONDITION_TYPE, this.getType()) + "\"";
	}

}
