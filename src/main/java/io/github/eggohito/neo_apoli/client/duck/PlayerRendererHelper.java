package io.github.eggohito.neo_apoli.client.duck;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface PlayerRendererHelper {

	default Player neo_apoli$getPlayer() {
		return null;
	}

	default void neo_apoli$setPlayer(@Nullable Player player) {

	}

}
