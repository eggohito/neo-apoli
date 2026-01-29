package io.github.eggohito.neo_apoli.api.key;

import io.github.eggohito.neo_apoli.impl.key.KeyStateManagerImpl;

import java.util.Optional;
import java.util.UUID;

public interface KeyStateManager {

	static Optional<KeyState> getState(UUID uuid, String key) {
		return Optional
			.ofNullable(KeyStateManagerImpl.STATES.get(uuid))
			.flatMap(map -> Optional.ofNullable(map.get(key)));
	}

}
