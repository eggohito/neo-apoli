package io.github.eggohito.neo_apoli.client.duck;

import io.github.eggohito.neo_apoli.util.color.Argb;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface EntityRenderCache {

	default Argb neo_apoli$getColor() {
		return null;
	}

	default Entity neo_apoli$getEntity() {
		return null;
	}

	default void neo_apoli$setColor(@Nullable Argb color) {

	}

	default void neo_apoli$setEntity(@Nullable Entity entity) {

	}

}
