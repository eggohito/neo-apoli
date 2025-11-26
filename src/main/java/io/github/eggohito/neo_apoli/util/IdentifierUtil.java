package io.github.eggohito.neo_apoli.util;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public class IdentifierUtil {

	public static ResourceLocation nonEmptySplit(String value) {

		if (value.isEmpty()) {
			throw new ResourceLocationException("Empty resource locations are not allowed!");
		}

		else {

			int separatorIndex = value.indexOf(ResourceLocation.NAMESPACE_SEPARATOR);

			if (separatorIndex >= 0) {

				String path = value.substring(separatorIndex + 1);
				String namespace = separatorIndex != 0
					? value.substring(0, separatorIndex)
					: ResourceLocation.DEFAULT_NAMESPACE;

				return nonEmptyOf(namespace, path);

			}

			else {
				return nonEmptyOf(ResourceLocation.DEFAULT_NAMESPACE, value);
			}

		}

	}

	public static ResourceLocation nonEmptyOf(String namespace, String path) {

		if (namespace.isEmpty() || path.isEmpty()) {
			throw new ResourceLocationException("Disallowed empty " + (namespace.isEmpty() ? "namespace" : "path") + " in resource location \"" + namespace + ":" + path + "\"");
		}

		else {
			return ResourceLocation.fromNamespaceAndPath(namespace, path);
		}

	}

}
