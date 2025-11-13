package io.github.eggohito.neo_apoli.condition.custom.meta;

import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;

public interface MetaCondition extends Condition {

	@Override
	default String asDisplayString() {
		return "Meta condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.CONDITION_TYPE, this.getType()) + "\"";
	}

}
