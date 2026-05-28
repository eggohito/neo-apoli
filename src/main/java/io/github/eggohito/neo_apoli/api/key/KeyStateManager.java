package io.github.eggohito.neo_apoli.api.key;

import io.github.eggohito.neo_apoli.impl.key.KeyStateManagerImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.UUID;

@ApiStatus.NonExtendable
public interface KeyStateManager {

	static Optional<KeyState> getState(UUID uuid, String key) {
		return KeyStateManagerImpl.getState(uuid, key);
	}

}
