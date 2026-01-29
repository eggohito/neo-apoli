package io.github.eggohito.neo_apoli.client.util;

import io.github.eggohito.neo_apoli.client.mixin.access.KeyMappingAccessor;
import io.github.eggohito.neo_apoli.util.alias.StringAlias;
import net.minecraft.client.KeyMapping;

import java.util.Optional;

public class KeyMappingUtil {

	public static final StringAlias ALIASES = new StringAlias();

	public static Optional<KeyMapping> getKeyMapping(String id) {
		return Optional.ofNullable(KeyMappingAccessor.getKeysById().get(ALIASES.resolveAlias(id)));
	}

}
