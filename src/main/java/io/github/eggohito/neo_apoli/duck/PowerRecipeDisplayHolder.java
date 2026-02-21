package io.github.eggohito.neo_apoli.duck;

import io.github.eggohito.neo_apoli.power.PowerReference;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import java.util.Map;

public interface PowerRecipeDisplayHolder {

	default Map<RecipeDisplayEntry, PowerReference> neo_apoli$getReferencesByDisplayEntry() {
		return new Object2ObjectOpenHashMap<>();
	}

	default void neo_apoli$setReferencesByDisplayEntry(Map<RecipeDisplayEntry, PowerReference> referencesByDisplay) {

	}

	default void neo_apoli$sendAll(ServerPlayer recipient) {

	}

}
