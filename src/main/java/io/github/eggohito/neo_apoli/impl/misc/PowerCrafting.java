package io.github.eggohito.neo_apoli.impl.misc;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import net.minecraft.world.inventory.TransientCraftingContainer;

import java.util.Map;

public interface PowerCrafting extends EntityCache {

	default TransientCraftingContainer neo_apoli$getInventory() {
		return null;
	}

	default void neo_apoli$setInventory(TransientCraftingContainer inventory) {

	}

	default Map<? extends Power.Instance<?>, Context> neo_apoli$getModifyingInstances() {
		return Map.of();
	}

	default void neo_apoli$setModifyingInstances(Map<? extends Power.Instance<?>, Context> modifyingInstances) {

	}

}
