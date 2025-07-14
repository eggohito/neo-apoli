package io.github.eggohito.neo_apoli.client.duck;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface PlayerRendererHelper {

	default PlayerEntity neo_apoli$getPlayer() {
		return null;
	}

	default void neo_apoli$setPlayer(@Nullable PlayerEntity player) {

	}

}
