package io.github.eggohito.neo_apoli.duck;

import net.minecraft.inventory.CraftingInventory;

public interface PowerCraftingInventory extends EntityCache {

	default CraftingInventory neo_apoli$getInventory() {
		return null;
	}

	default void neo_apoli$setInventory(CraftingInventory inventory) {

	}

}
