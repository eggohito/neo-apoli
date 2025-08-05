package io.github.eggohito.neo_apoli.client.duck;

import io.github.eggohito.neo_apoli.duck.EntityCache;
import io.github.eggohito.neo_apoli.util.color.Argb;
import org.jetbrains.annotations.Nullable;

public interface EntityRenderCache extends EntityCache {

	default Argb neo_apoli$getColor() {
		return null;
	}

	default void neo_apoli$setColor(@Nullable Argb color) {

	}

}
