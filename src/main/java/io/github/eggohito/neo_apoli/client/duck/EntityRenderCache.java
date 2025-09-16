package io.github.eggohito.neo_apoli.client.duck;

import io.github.eggohito.neo_apoli.duck.EntityCache;

public interface EntityRenderCache extends EntityCache {

	default int neo_apoli$getColor() {
		return -1;
	}

	default void neo_apoli$setColor(int color) {

	}

}
