package io.github.eggohito.neo_apoli.resource.json;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.parsers.json.JsonFormat;

import java.util.*;

@Getter
@Accessors(fluent = true)
public class JsonFileToIdConverter extends FileToIdConverter {

	private final String directory;
	private final EnumSet<JsonFormat> formats;

	public JsonFileToIdConverter(String directory, EnumSet<JsonFormat> formats) {
		super(directory, ".json");
		this.directory = directory;
		this.formats = formats;
	}

	@Override
	public @NotNull Map<ResourceLocation, Resource> listMatchingResources(ResourceManager manager) {
		return manager.listResources(directory(), this::isSupported);
	}

	@Override
	public @NotNull Map<ResourceLocation, List<Resource>> listMatchingResourceStacks(ResourceManager manager) {
		return manager.listResourceStacks(directory(), this::isSupported);
	}

	@Override
	public @NotNull ResourceLocation fileToId(ResourceLocation file) {
		return file
			.withPath(FilenameUtils::removeExtension)
			.withPath(path -> path.substring(directory().length() + 1));
	}

	@Override
	public @NotNull ResourceLocation idToFile(ResourceLocation id) {
		return this.idToFile(id, JsonFormat.JSON);
	}

	public ResourceLocation idToFile(ResourceLocation id, JsonFormat format) {
		return id
			.withPrefix(directory())
			.withSuffix("." + format.toString().toLowerCase(Locale.ROOT));
	}

	public JsonFormat getFormat(ResourceLocation file) {

		String extension = FilenameUtils.getExtension(file.getPath());

		for (var format : formats) {

			String formatExtension = format.toString();

			if (extension.equalsIgnoreCase(formatExtension)) {
				return format;
			}

		}

		throw new NoSuchElementException("No supported JSON formats were found for file: \"" + file + "\"");

	}

	public boolean isSupported(ResourceLocation file) {

		try {
			return getFormat(file) != null;
		}

		catch (NoSuchElementException ignored) {
			return false;
		}

	}

	public static JsonFileToIdConverter of(String directory) {
		return new JsonFileToIdConverter(directory, EnumSet.allOf(JsonFormat.class));
	}

	public static JsonFileToIdConverter registry(ResourceKey<? extends Registry<?>> registryKey) {
		return of(Registries.elementsDirPath(registryKey));
	}

}
