package io.github.eggohito.neo_apoli.util;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class IdentifierUtil {

	public static Identifier emptyStrictSplit(String value) {

		if (value.isEmpty()) {
			throw new InvalidIdentifierException("Empty resource locations are not allowed!");
		}

		else {

			int separatorIndex = value.indexOf(Identifier.NAMESPACE_SEPARATOR);

			if (separatorIndex >= 0) {

				String path = value.substring(separatorIndex + 1);
				String namespace = separatorIndex != 0
					? value.substring(0, separatorIndex)
					: Identifier.DEFAULT_NAMESPACE;

				return emptyStrictOf(namespace, path);

			}

			else {
				return emptyStrictOf(Identifier.DEFAULT_NAMESPACE, value);
			}

		}

	}

	public static Identifier emptyStrictOf(String namespace, String path) {

		if (namespace.isEmpty() || path.isEmpty()) {
			throw new InvalidIdentifierException("Disallowed empty " + (namespace.isEmpty() ? "namespace" : "path") + " in resource location \"" + namespace + ":" + path + "\"");
		}

		else {
			return Identifier.of(namespace, path);
		}

	}

}
