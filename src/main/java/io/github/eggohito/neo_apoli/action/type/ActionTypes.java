package io.github.eggohito.neo_apoli.action.type;

import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;

public final class ActionTypes {

	public static void registerAll() {
		BlockActionTypes.registerAll();
		EntityActionTypes.registerAll();
	}

}
