package io.github.eggohito.neo_apoli.action.type;

import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.action.type.meta.MetaActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class ActionTypes {

	public static void registerAll() {
		BiEntityActionTypes.registerAll();
		BlockActionTypes.registerAll();
		EntityActionTypes.registerAll();
		ItemActionTypes.registerAll();
		MetaActionTypes.registerAll();
	}

	public static <A extends Action, T extends ActionType<A>> T register(ResourceLocation id, T type) {
		return Registry.register(NeoApoliRegistries.ACTION_TYPE, id, type);
	}

}
