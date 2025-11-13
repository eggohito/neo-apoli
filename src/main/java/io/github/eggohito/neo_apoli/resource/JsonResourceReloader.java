package io.github.eggohito.neo_apoli.resource;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;
import org.quiltmc.parsers.json.JsonFormat;

import java.util.Map;
import java.util.NoSuchElementException;

public interface JsonResourceReloader extends ResourceReloader, IdentifiableResourceReloadListener {

	Map<String, JsonFormat> DEFAULT_JSON_FORMATS = Map.of(
		"json", JsonFormat.JSON,
		"json5", JsonFormat.JSON5,
		"jsonc", JsonFormat.JSONC
	);

	default Map<String, JsonFormat> getSupportedJsonFormats() {
		return DEFAULT_JSON_FORMATS;
	}

	default JsonFormat getJsonFormat(Identifier fileId) {

		String fileExtension = FilenameUtils.getExtension(fileId.getPath());
		JsonFormat format = this.getSupportedJsonFormats().get(fileExtension);

		if (format != null) {
			return format;
		}

		else {
			throw new NoSuchElementException("No supported JSON formats was found for file extension: '" + fileExtension + "'");
		}

	}

	default Identifier trimExtension(Identifier fileId, String directory) {
		return Identifier.of(fileId.getNamespace(), FilenameUtils.removeExtension(fileId.getPath()).substring(directory.length() + 1));
	}

	default boolean supportsJsonFormat(Identifier fileId) {

		for (String supportedJsonFileExtension : this.getSupportedJsonFormats().keySet()) {

			if (fileId.getPath().endsWith(supportedJsonFileExtension)) {
				return true;
			}

		}

		return false;

	}

	interface Entry {

		String source();

		JsonElement element();

	}

}
