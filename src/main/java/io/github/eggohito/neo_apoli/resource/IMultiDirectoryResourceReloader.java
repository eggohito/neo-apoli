package io.github.eggohito.neo_apoli.resource;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;
import org.quiltmc.parsers.json.JsonFormat;

import java.util.Map;
import java.util.Set;

public interface IMultiDirectoryResourceReloader extends IdentifiableResourceReloadListener {

	Map<String, JsonFormat> getSupportedJsonFormats();

	Set<String> getDirectories();

	default Identifier trimExtension(Identifier fileId, String directory) {
		String path = FilenameUtils.removeExtension(fileId.getPath()).substring(directory.length() + 1);
		return Identifier.of(fileId.getNamespace(), path);
	}

	default boolean supportsJsonFormat(Identifier fileId) {

		Set<String> supportedJsonFormats = this.getSupportedJsonFormats().keySet();

		for (String supportedJsonFormat : supportedJsonFormats) {

			if (fileId.getPath().endsWith(supportedJsonFormat)) {
				return true;
			}

		}

		return false;

	}

	record Entry(String source, JsonElement element) {

	}

}
