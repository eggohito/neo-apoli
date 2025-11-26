package io.github.eggohito.neo_apoli.duck;

import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public interface PowerRecipeDisplayHolder {

	default Map<RecipeDisplayEntry, PowerReference> neo_apoli$getReferencesByDisplayEntry() {
		return new Object2ObjectOpenHashMap<>();
	}

	default void neo_apoli$setReferencesByDisplayEntry(Map<RecipeDisplayEntry, PowerReference> referencesByDisplay) {

	}

}
