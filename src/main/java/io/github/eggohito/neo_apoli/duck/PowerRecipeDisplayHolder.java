package io.github.eggohito.neo_apoli.duck;

import io.github.eggohito.neo_apoli.power.PowerReference;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.level.ServerPlayer;

public interface PowerRecipeDisplayHolder {

	default Int2ObjectMap<PowerReference> neo_apoli$getReferencesByIndex() {
		return new Int2ObjectOpenHashMap<>();
	}

	default void neo_apoli$setReferencesByIndex(Int2ObjectMap<PowerReference> referencesById) {

	}

	default void neo_apoli$sendAll(ServerPlayer recipient) {

	}

}
