package io.github.eggohito.neo_apoli.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.fabricmc.fabric.mixin.resource.conditions.RegistryOpsAccessor;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.FilenameUtils;
import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.Map;
import java.util.Set;

public abstract class MultiDirectoryResourceReloader extends SinglePreparationResourceReloader<Map<Identifier, MultiDirectoryResourceReloader.Entry>> implements IdentifiableResourceReloadListener {

	protected static final Logger LOGGER = LoggerFactory.getLogger(MultiDirectoryResourceReloader.class);

	protected final Gson gson;
	protected final ResourceType resourceType;

	protected final RegistryWrapper.WrapperLookup wrapperLookup;
	protected final RegistryOps<JsonElement> ops;

	public MultiDirectoryResourceReloader(Gson gson, ResourceType resourceType, RegistryWrapper.WrapperLookup wrapperLookup) {
		this.gson = gson;
		this.resourceType = resourceType;
		this.wrapperLookup = wrapperLookup;
		this.ops = wrapperLookup.getOps(JsonOps.INSTANCE);
	}

	@Override
	protected Map<Identifier, Entry> prepare(ResourceManager manager, Profiler profiler) {

		Map<Identifier, Entry> prepared = new Object2ObjectOpenHashMap<>();
		Set<String> directories = this.getDirectories();

		String simpleClassName = this.getClass().getSimpleName();
		String packType = resourceType == ResourceType.CLIENT_RESOURCES
			? "resource"
			: "data";

		for (String directory : directories) {

			Map<Identifier, Resource> resources = manager.findResources(directory, this::supportsJsonFormat);
			profiler.push("[" + simpleClassName + "] scanning files in directory \"" + directory + "\" from " + packType + " packs");

			for (Map.Entry<Identifier, Resource> resourceEntry : resources.entrySet()) {

				Identifier fileId = resourceEntry.getKey();
				String fileExtension = "." + FilenameUtils.getExtension(fileId.getPath());

				Identifier resourceId = this.trimExtension(fileId, directory);
				Resource resource = resourceEntry.getValue();

				JsonFormat jsonFormat = this.getSupportedJsonFormats().get(fileExtension);
				String packName = resource.getPackId();

				profiler.push("[" + simpleClassName + "] preparing file \"" + fileId + "\" from " + packType + " {" + packName + "}");

				if (prepared.containsKey(resourceId)) {
					LOGGER.warn("Ignored duplicate JSON file with ID \"{}\" from directory \"{}\" of " + packType + " pack [{}]!", resourceId, directory, packName);
				}

				else {

					try (BufferedReader reader = resource.getReader()) {

						GsonReader gsonReader = new GsonReader(JsonReader.create(reader, jsonFormat));
						JsonElement jsonElement = gson.fromJson(gsonReader, JsonElement.class);

						if (jsonElement == null) {
							throw new JsonParseException("JSON cannot be empty!");
						}

						else {
							this.prepareSingle(prepared, directory, resourceId, new Entry(packName, jsonElement));
						}

					}

					catch (Exception e) {
						LOGGER.error("Error trying to prepare JSON for file \"{}\" from {} pack [{}] (skipping): {}", fileId, packType, packName, e);
					}

				}

				profiler.pop();

			}

			profiler.pop();

		}

		return prepared;

	}

	protected void prepareSingle(Map<Identifier, Entry> prepared, String directory, Identifier resourceId, Entry entry) {
		prepared.put(resourceId, entry);
	}

	protected abstract Map<String, JsonFormat> getSupportedJsonFormats();

	protected abstract Set<String> getDirectories();

	protected Identifier trimExtension(Identifier fileId, String directory) {
		String path = FilenameUtils.removeExtension(fileId.getPath()).substring(directory.length() + 1);
		return Identifier.of(fileId.getNamespace(), path);
	}

	protected boolean supportsJsonFormat(Identifier fileId) {

		Set<String> supportedJsonFormats = this.getSupportedJsonFormats().keySet();

		for (String supportedJsonFormat : supportedJsonFormats) {

			if (fileId.getPath().endsWith(supportedJsonFormat)) {
				return true;
			}

		}

		return false;

	}

	@SuppressWarnings("UnstableApiUsage")
	public static boolean isResourceConditionFulfilled(Identifier resourceId, JsonObject jsonObject, String directory, RegistryOps<JsonElement> ops) {
		return ResourceConditionsImpl.applyResourceConditions(jsonObject, directory, resourceId, ((RegistryOpsAccessor) ops).getRegistryInfoGetter());
	}

	public static boolean isResourceConditionFulfilled(Identifier resourceId, JsonElement jsonElement, String directory, RegistryOps<JsonElement> ops) {
		return !(jsonElement instanceof JsonObject jsonObject)
			|| isResourceConditionFulfilled(resourceId, jsonObject, directory, ops);
	}

	public record Entry(String source, JsonElement element) {

	}

}
