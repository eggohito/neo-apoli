package io.github.eggohito.neo_apoli.duck;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface EntityCache {

	@Nullable
	default Entity neo_apoli$getEntity() {
		return null;
	}

	default void neo_apoli$setEntity(@Nullable Entity entity) {

	}

}
