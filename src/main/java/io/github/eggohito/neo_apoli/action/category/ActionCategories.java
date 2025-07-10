package io.github.eggohito.neo_apoli.action.category;

import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.registry.Registry;

public final class ActionCategories {

	public static final BiEntityActionCategory BIENTITY_ACTION = register(new BiEntityActionCategory());
	public static final BlockActionCategory BLOCK_ACTION = register(new BlockActionCategory());
	public static final EntityActionCategory ENTITY_ACTION = register(new EntityActionCategory());
	public static final ItemActionCategory ITEM_ACTION = register(new ItemActionCategory());

	public static void registerAll() {

	}

	public static <A extends Action, C extends ActionCategory<A>> C register(C category) {
		return Registry.register(NeoApoliRegistries.ACTION_CATEGORY, category.registryRef().getValue(), category);
	}

}
