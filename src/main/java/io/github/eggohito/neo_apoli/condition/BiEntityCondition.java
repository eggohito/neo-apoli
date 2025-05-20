package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ReferenceBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface BiEntityCondition extends Condition<BiEntityConditionType<?>> {

	Codec<BiEntityCondition> CODEC = Codec.recursive(BiEntityCondition.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BiEntityConditionTypes.CODEC.dispatch(TYPE_KEY, BiEntityCondition::getType, BiEntityConditionType::mapCodec), Identifier.CODEC.xmap(ReferenceBiEntityCondition::new, ReferenceBiEntityCondition::value)));
	PacketCodec<RegistryByteBuf, BiEntityCondition> PACKET_CODEC = BiEntityConditionTypes.PACKET_CODEC.dispatch(BiEntityCondition::getType, BiEntityConditionType::packetCodec);

	@Override
	default ConditionCategory<BiEntityCondition> getCategory() {
		return ConditionCategories.BIENTITY_CONDITION;
	}

	@Override
	default String asDisplayString() {
		return ConditionManager.getIdAsResult(this)
			.result()
			.map(id -> "Bi-entity condition with ID \"" + id + "\"")
			.orElseGet(() -> "Bi-entity condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, this.getType()) + "\"");
	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.ACTOR, ContextParameters.TARGET);
	}

}
