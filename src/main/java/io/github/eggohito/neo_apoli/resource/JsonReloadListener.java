package io.github.eggohito.neo_apoli.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.apache.commons.io.FilenameUtils;
import org.quiltmc.parsers.json.JsonFormat;

import java.util.Map;
import java.util.NoSuchElementException;

public interface JsonReloadListener extends PreparableReloadListener, IdentifiableResourceReloadListener {

	Map<String, JsonFormat> DEFAULT_JSON_FORMATS = Map.of(
		"json", JsonFormat.JSON,
		"json5", JsonFormat.JSON5,
		"jsonc", JsonFormat.JSONC
	);

	default Map<String, JsonFormat> getSupportedFormats() {
		return DEFAULT_JSON_FORMATS;
	}

	default JsonFormat getFormat(ResourceLocation fileId) {

		String fileExtension = FilenameUtils.getExtension(fileId.getPath());
		JsonFormat format = this.getSupportedFormats().get(fileExtension);

		if (format != null) {
			return format;
		}

		else {
			throw new NoSuchElementException("No supported JSON formats was found for file extension: '" + fileExtension + "'");
		}

	}

	default ResourceLocation trimExtension(ResourceLocation fileId, String directory) {
		return ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), FilenameUtils.removeExtension(fileId.getPath()).substring(directory.length() + 1));
	}

	default boolean supportsFormat(ResourceLocation fileId) {

		for (String supportedJsonFileExtension : this.getSupportedFormats().keySet()) {

			if (fileId.getPath().endsWith(supportedJsonFileExtension)) {
				return true;
			}

		}

		return false;

	}

	interface ElementWithSource {

		String source();

		JsonElement element();

	}

	record ObjectElementWithSource(String source, JsonObject element) implements ElementWithSource {

	}

}
