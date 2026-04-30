package io.github.eggohito.neo_apoli.client.api.misc;

import io.github.eggohito.neo_apoli.api.misc.EntityCache;

/**
 *  An interface injected to {@link net.minecraft.client.renderer.entity.state.EntityRenderState} for storing a reference
 *  to the entity being rendered, and a packed color value (for overriding colors)
 */
public interface EntityRenderCache extends EntityCache {

	default int neo_apoli$getColor() {
		return -1;
	}

	default void neo_apoli$setColor(int color) {

	}

}
