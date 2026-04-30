package io.github.eggohito.neo_apoli.impl.misc;

import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.level.ServerPlayer;

public interface PowerRecipeDisplayHolder {

	default Int2ObjectMap<PowerIdentifier> neo_apoli$getPowerIdsByIndex() {
		return new Int2ObjectOpenHashMap<>();
	}

	default void neo_apoli$setPowerIdsByIndex(Int2ObjectMap<PowerIdentifier> powerIdsByIndex) {

	}

	default void neo_apoli$sendAll(ServerPlayer recipient) {

	}

}
