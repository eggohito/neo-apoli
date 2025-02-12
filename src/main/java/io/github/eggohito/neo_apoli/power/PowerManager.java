package io.github.eggohito.neo_apoli.power;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.util.PowerIdentifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PowerManager extends SinglePreparationResourceReloader<Map<Identifier, PowerManager.PackData>> implements IdentifiableResourceReloadListener {
	
	private static final Map<String, JsonFormat> JSON_FORMATS = Map.of(
		".json", JsonFormat.JSON,
		".json5", JsonFormat.JSON5,
		".jsonc", JsonFormat.JSONC
	);

	public static final Set<String> DIRECTORY_PATHS = Util.make(new ObjectOpenHashSet<>(), set -> {
		set.add("power");
		set.add("apoli/power");
	});

	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();
	
	private static final Identifier ID = NeoApoli.id("powers");
	private static final Set<Identifier> DEPENDENCIES = new ObjectOpenHashSet<>();

	private static final Object2ObjectOpenHashMap<PowerIdentifier, Power> POWERS_BY_ID = new Object2ObjectOpenHashMap<>();
	private static boolean init;

	private final RegistryOps<JsonElement> jsonOps;

	PowerManager(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.jsonOps = wrapperLookup.getOps(JsonOps.INSTANCE);
	}

	@ApiStatus.Internal
	public static void init() {

		if (init) {
			throw new RuntimeException("Power manager is already initialized!!");
		}

		else {
			ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, PowerManager::new);
			init = true;
		}

	}

	@Override
	protected Map<Identifier, PackData> prepare(ResourceManager manager, Profiler profiler) {

		Map<Identifier, PackData> prepared = new Object2ObjectOpenHashMap<>();
		profiler.push("neo-apoli::preparePowers");

		for (String directoryPath : DIRECTORY_PATHS) {

			profiler.push("neo-apoli::preparePowers::scan->" + directoryPath);
			manager.findAllResources(directoryPath, PowerManager::hasValidFormat).forEach((fileId, resources) -> {

				Identifier resourceId = trimExtension(fileId, directoryPath);
				String fileExtension = "." + FilenameUtils.getExtension(fileId.getPath());

				JsonFormat jsonFormat = JSON_FORMATS.get(fileExtension);
				profiler.push("neo-apoli::preparePowers::find->" + fileId);

				resources.forEach(resource -> {

					String packName = resource.getPackId();
					profiler.push("neo-apoli::preparePowers::startPrep->" + fileId + "::[" + packName + "]");

					try (BufferedReader reader = resource.getReader()) {

						GsonReader gsonReader = new GsonReader(JsonReader.create(reader, jsonFormat));
						JsonElement jsonElement = GSON.fromJson(gsonReader, JsonElement.class);

						if (jsonElement == null) {
							throw new JsonParseException("JSON cannot be empty!");
						}

						else {
							prepared.put(resourceId, new PackData(packName, jsonElement));
						}

					}

					catch (Exception e) {
						NeoApoli.LOGGER.error("Error trying to prepare JSON of power \"{}\" from data pack [{}] (skipping): {}", resourceId, packName, e);
					}

					profiler.pop();

				});

				profiler.pop();

			});

			profiler.pop();

		}

		profiler.pop();
		return prepared;

	}

	@Override
	protected void apply(Map<Identifier, PackData> prepared, ResourceManager manager, Profiler profiler) {

		POWERS_BY_ID.clear();
		NeoApoli.LOGGER.info("Parsing powers from data packs...");

		profiler.push("neo-apoli::parsePowers");
		prepared.forEach((id, packData) -> {

			profiler.push("neo-apoli::parsePowers::startParse->" + id + "::[" + packData.source() + "]");
			try {

				PowerIdentifier powerId = PowerIdentifier.of(id);
				Power power = Power.BASE_CODEC.decode(jsonOps, packData.element())
					.getOrThrow()
					.getFirst();

				if (!POWERS_BY_ID.containsKey(powerId)) {

					if (power instanceof MultiplePower multiplePower) {
						multiplePower.getSubPowers().forEach((name, subPower) -> POWERS_BY_ID.put(PowerIdentifier.subPower(id, name), subPower));
					}

					POWERS_BY_ID.put(powerId, power);

				}

			}

			catch (Exception e) {
				String message = e.getMessage();
				NeoApoli.LOGGER.error("Error trying to parse power \"{}\" from data pack [{}] (skipping): {}", id, packData.source(), (message == null || message.isEmpty() ? e : message));
			}

			profiler.pop();

		});

		profiler.pop();

		NeoApoli.LOGGER.info("Finished parsing powers from data packs. Contains {} power(s).", POWERS_BY_ID.size());
		POWERS_BY_ID.trim();

	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return DEPENDENCIES;
	}

	public static DataResult<Power> getAsResult(PowerIdentifier powerId) {
		return contains(powerId)
			? DataResult.success(POWERS_BY_ID.get(powerId))
			: DataResult.error(() -> "No powers with power identifier \"" + powerId + "\" were found!");
	}

	public static Set<PowerIdentifier> getIds() {
		return POWERS_BY_ID.keySet();
	}

	public static boolean contains(PowerIdentifier powerId) {
		return POWERS_BY_ID.containsKey(powerId);
	}

	private static Identifier trimExtension(Identifier fileId, String directoryPath) {
		String path = FilenameUtils.removeExtension(fileId.getPath()).substring(directoryPath.length() + 1);
		return Identifier.of(fileId.getNamespace(), path);
	}

	private static boolean hasValidFormat(Identifier fileId) {
		return JSON_FORMATS.keySet()
			.stream()
			.anyMatch(suffix -> fileId.getPath().endsWith(suffix));
	}

	public record PackData(String source, JsonElement element) {

	}

}
