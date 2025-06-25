package io.github.eggohito.neo_apoli.action.type;

import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;

public final class ActionTypes {

	public static void registerAll() {
		BiEntityActionTypes.registerAll();
		BlockActionTypes.registerAll();
		EntityActionTypes.registerAll();
		ItemActionTypes.registerAll();
	}

}
