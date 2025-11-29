package io.github.eggohito.neo_apoli.util;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationUtil {

	public static ResourceLocation nonEmptyOf(String namespace, String path) {

		if (namespace.isEmpty() || path.isEmpty()) {
			throw new ResourceLocationException("Disallowed empty " + (namespace.isEmpty() ? "namespace" : "path") + " in resource location \"" + namespace + ":" + path + "\"");
		}

		else {
			return ResourceLocation.fromNamespaceAndPath(namespace, path);
		}

	}

}
