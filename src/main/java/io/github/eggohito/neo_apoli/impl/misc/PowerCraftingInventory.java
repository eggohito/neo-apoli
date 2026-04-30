package io.github.eggohito.neo_apoli.impl.misc;

import io.github.eggohito.neo_apoli.api.misc.EntityCache;
import net.minecraft.world.inventory.TransientCraftingContainer;

public interface PowerCraftingInventory extends EntityCache {

	default TransientCraftingContainer neo_apoli$getInventory() {
		return null;
	}

	default void neo_apoli$setInventory(TransientCraftingContainer inventory) {

	}

}
