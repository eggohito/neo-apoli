package io.github.eggohito.neo_apoli.util.alias;

import io.github.eggohito.neo_apoli.exception.AliasAlreadyTakenException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class StringAlias {

	private final Map<String, String> aliases;

	public StringAlias() {
		this.aliases = new Object2ObjectOpenHashMap<>();
	}

	public String resolveAlias(String value) {

		if (hasAlias(value)) {
			return aliases.get(value);
		}

		else {
			return value;
		}

	}

	public void addAlias(String from, String to) {

		if (aliases.putIfAbsent(from, to) != null) {
			throw new AliasAlreadyTakenException(from, to, () -> aliases.get(from));
		}

	}

	public boolean hasAlias(String value) {
		return aliases.containsKey(value);
	}

}
