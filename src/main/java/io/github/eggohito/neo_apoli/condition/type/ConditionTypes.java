package io.github.eggohito.neo_apoli.condition.type;

import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;

public final class ConditionTypes {

	public static void registerAll() {
		BlockConditionTypes.registerAll();
		EntityConditionTypes.registerAll();
	}

}
