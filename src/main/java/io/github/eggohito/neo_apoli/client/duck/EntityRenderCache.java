package io.github.eggohito.neo_apoli.client.duck;

import io.github.eggohito.neo_apoli.util.color.Argb;
import net.minecraft.entity.Entity;

public interface EntityRenderCache {

	default Argb neo_apoli$getColor() {
		return null;
	}

	default Entity neo_apoli$getEntity() {
		return null;
	}

	default void neo_apoli$setColor(Argb color) {

	}

	default void neo_apoli$setEntity(Entity entity) {

	}

}
