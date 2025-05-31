package io.github.eggohito.neo_apoli.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class StringAlias {

	public static final StringAlias GLOBAL = new StringAlias();

	private final Map<String, String> aliases;

	public StringAlias() {
		this.aliases = new Object2ObjectOpenHashMap<>();
	}

	public String resolveAlias(String value) {

		if (hasAlias(value)) {
			return aliases.get(value);
		}

		else if (this != GLOBAL) {
			return GLOBAL.resolveAlias(value);
		}

		else {
			return value;
		}

	}

	public boolean addAlias(String from, String to) {
		return aliases.putIfAbsent(from ,to) != null;
	}

	public boolean hasAlias(String value) {
		return aliases.containsKey(value)
			|| (this != GLOBAL && GLOBAL.hasAlias(value));
	}

}
