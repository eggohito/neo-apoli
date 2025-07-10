package io.github.eggohito.neo_apoli.client.duck;

import net.minecraft.entity.Entity;

public interface EntityRenderCache {

	default Entity neo_apoli$getEntity() {
		return null;
	}

	default void neo_apoli$setEntity(Entity entity) {

	}

}
