package io.github.eggohito.neo_apoli.util;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationUtil {

	public static ResourceLocation nonEmpty(ResourceLocation id) {

		if (id.getNamespace().isEmpty() || id.getPath().isEmpty()) {
			throw new ResourceLocationException("Disallowed empty " + (id.getNamespace().isEmpty() ? "namespace" : "path") + " in resource location \"" + id + "\"");
		}

		else {
			return id;
		}

	}

}
