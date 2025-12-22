package io.github.eggohito.neo_apoli.resource.json;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.apache.commons.io.FilenameUtils;
import org.quiltmc.parsers.json.JsonFormat;

import java.util.Locale;
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

		String extension = this.getExtension(fileId);
		JsonFormat format = this.getSupportedFormats().get(extension);

		if (format != null) {
			return format;
		}

		else {
			throw new NoSuchElementException("No supported JSON formats was found for file extension: '" + extension + "'");
		}

	}

	default String getExtension(ResourceLocation fileId) {
		return FilenameUtils.getExtension(fileId.getPath()).toLowerCase(Locale.ROOT);
	}

	default ResourceLocation trimExtension(ResourceLocation fileId, String directory) {
		return ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), FilenameUtils.removeExtension(fileId.getPath()).substring(directory.length() + 1));
	}

	default boolean supportsFormat(ResourceLocation fileId) {

		for (String supportedExtension : this.getSupportedFormats().keySet()) {

			String extension = this.getExtension(fileId);

			if (extension.equalsIgnoreCase(supportedExtension)) {
				return true;
			}

		}

		return false;

	}

}
