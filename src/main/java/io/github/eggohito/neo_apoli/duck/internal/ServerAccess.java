package io.github.eggohito.neo_apoli.duck.internal;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

public interface ServerAccess {

	MinecraftServer neo_apoli$getServer();

	@ApiStatus.Internal
	default void neo_apoli$setServer(MinecraftServer server) {

	}

}
