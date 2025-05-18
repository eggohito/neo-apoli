package io.github.eggohito.neo_apoli.condition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeConditionsS2CPacket;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.resource.IMultiDirectoryResourceReloader;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ConditionManager extends SinglePreparationResourceReloader<Map<ConditionCategory<?>, Map<Identifier, IMultiDirectoryResourceReloader.Entry>>> implements IMultiDirectoryResourceReloader {

	private static final Set<String> DIRECTORY_PREFIXES = new ObjectOpenHashSet<>();

	private static final Logger LOGGER = LoggerFactory.getLogger(ConditionManager.class);
	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	public static final Identifier ID = NeoApoli.id("conditions");
	public static final Set<Identifier> DEPENDENCIES = new ObjectOpenHashSet<>();

	private static final Object2ObjectOpenHashMap<ConditionCategory<?>, Map<Identifier, ConditionEntry<?>>> BY_CATEGORY_AND_ID = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<Condition<?>, Identifier> BY_VALUES = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> ops;

	public ConditionManager(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.ops = wrapperLookup.getOps(JsonOps.INSTANCE);
	}

	@ApiStatus.Internal
	public static void init() {

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, ConditionManager::new);
		addDirectoryPrefix(NeoApoli.MOD_NAMESPACE);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));

	}

	@Override
	protected Map<ConditionCategory<?>, Map<Identifier, Entry>> prepare(ResourceManager manager, Profiler profiler) {

		Map<ConditionCategory<?>, Map<Identifier, Entry>> prepared = new Object2ObjectOpenHashMap<>();

		for (ConditionCategory<?> category : NeoApoliRegistries.CONDITION_CATEGORY) {

			Set<String> directories = this.getDirectories();

			for (String directory : directories) {

				Map<Identifier, Resource> resources = manager.findResources(directory, this::supportsJsonFormat);
				String uncapitalizedCategory = StringUtils.uncapitalize(category.toString());

				profiler.push("[" + ConditionManager.class.getSimpleName() + "] scanning " + uncapitalizedCategory + " files in directory \"" + directory + "\" from data packs");

				for (Map.Entry<Identifier, Resource> resourceEntry : resources.entrySet()) {

					Identifier fileId = resourceEntry.getKey();
					String fileExtension = "." + FilenameUtils.getExtension(fileId.getPath());

					Identifier resourceId = this.trimExtension(fileId, directory);
					Resource resource = resourceEntry.getValue();

					JsonFormat jsonFormat = this.getSupportedJsonFormats().get(fileExtension);
					String packName = resource.getPackId();

					profiler.push("[" + ConditionManager.class.getSimpleName() + "] preparing " + uncapitalizedCategory + " file \"" + fileId + "\" from data pack {" + packName + "}");

					if (prepared.containsKey(category) && prepared.get(category).containsKey(resourceId)) {
						LOGGER.warn("Ignored duplicate {} JSON file with ID \"{}\" from directory \"{}\" of data pack [{}]!", uncapitalizedCategory, resourceId, directory, packName);
					}

					else {

						try (BufferedReader reader = resource.getReader()) {

							GsonReader gsonReader = new GsonReader(JsonReader.create(reader, jsonFormat));
							JsonElement jsonElement = GSON.fromJson(gsonReader, JsonElement.class);

							if (jsonElement != null) {
								prepared
									.computeIfAbsent(category, k -> new Object2ObjectOpenHashMap<>())
									.put(resourceId, new Entry(packName, jsonElement));
							}

							else {
								throw new JsonSyntaxException("JSON file cannot be empty!");
							}

						}

						catch (Exception e) {
							LOGGER.error("Error trying to prepare {} JSON file \"{}\" from data pack [{}] (skipping): {}", uncapitalizedCategory, fileId, packName, e.getMessage());
						}

					}

					profiler.pop();

				}

				profiler.pop();

			}

		}

		return prepared;

	}

	@Override
	protected void apply(Map<ConditionCategory<?>, Map<Identifier, Entry>> prepared, ResourceManager manager, Profiler profiler) {

		LOGGER.info("Parsing conditions from data packs...");
		profiler.push("[" + ConditionManager.class.getSimpleName() + "] start parsing conditions");

		startLoading();
		prepared.forEach((category, entries) -> entries.forEach((id, entry) -> {

			String uncapitalizedCategoryName = StringUtils.uncapitalize(category.toString());
			profiler.push("[" + ConditionManager.class.getSimpleName() + "] parsing " + uncapitalizedCategoryName + " JSON \"" + id + "\" from data pack {" + entry.source() + "}");

			try {
				register(new ConditionEntry<>(id, category.codec().parse(ops, entry.element()).getOrThrow()));
			}

			catch (Exception e) {
				LOGGER.error("Error trying to parse {} \"{}\" from data pack [{}] (skipping): {}", uncapitalizedCategoryName, id, entry.source(), e.getMessage());
			}

			profiler.pop();

		}));

	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return DEPENDENCIES;
	}

	@Override
	public Map<String, JsonFormat> getSupportedJsonFormats() {
		return NeoApoli.JSON_FORMATS;
	}

	@Override
	public Set<String> getDirectories() {

		Set<String> directories = new ObjectOpenHashSet<>();

		for (ConditionCategory<?> category : NeoApoliRegistries.CONDITION_CATEGORY) {
			directories.addAll(this.getDirectories(category));
		}

		return directories;

	}

	public Set<String> getDirectories(ConditionCategory<?> category) {

		String directory = category.directory();
		Set<String> directories = ObjectOpenHashSet.of(directory);

		for (String prefix : DIRECTORY_PREFIXES) {
			directories.add(prefix + "/" + directory);
		}

		return directories;

	}

	@ApiStatus.Internal
	public static void sendSyncPayload(ServerPlayerEntity player) {

		if (!player.server.isRemote()) {
			return;
		}

		Set<ConditionEntry<?>> entries = BY_CATEGORY_AND_ID.values()
			.stream()
			.map(Map::values)
			.flatMap(Collection::stream)
			.collect(Collectors.toCollection(ObjectOpenHashSet::new));

		LOGGER.info("Sent {} condition(s) to player {}!", entries.size(), player.getName().getString());
		ServerPlayNetworking.send(player, new SynchronizeConditionsS2CPacket(entries));

	}

	@ApiStatus.Internal
	public static void receiveSyncPayload(SynchronizeConditionsS2CPacket payload) {
		startLoading();
		payload.conditions().forEach(ConditionManager::register);
		endLoading();
	}

	@SuppressWarnings("unchecked")
	public static <C extends Condition<?>> DataResult<ConditionEntry<C>> getEntryAsResult(ConditionCategory<C> category, Identifier id) {

		if (BY_CATEGORY_AND_ID.containsKey(category)) {
			ConditionEntry<?> entry = BY_CATEGORY_AND_ID.get(category).get(id);
			return entry != null
				? DataResult.success((ConditionEntry<C>) entry)
				: DataResult.error(() -> category + " with ID \"" + id + "\" does not exist!");
		}

		else {
			return DataResult.error(() -> "No " + StringUtils.uncapitalize(category.toString()) + "s are registered!");
		}

	}

	public static <C extends Condition<?>> ConditionEntry<C> getEntry(ConditionCategory<C> category, Identifier id) {
		return getEntryAsResult(category, id).getOrThrow(IllegalArgumentException::new);
	}

	public static <C extends Condition<?>> DataResult<C> getAsResult(ConditionCategory<C> category, Identifier id) {
		return getEntryAsResult(category, id).map(ConditionEntry::value);
	}

	public static <C extends Condition<?>> C get(ConditionCategory<C> category, Identifier id) {
		return getAsResult(category, id).getOrThrow(IllegalArgumentException::new);
	}

	public static <C extends Condition<?>> DataResult<Identifier> getIdAsResult(C condition) {
		return containsId(condition)
			? DataResult.success(BY_VALUES.get(condition))
			: DataResult.error(() -> condition.asDisplayString(true) + " doesn't correspond to any identifiers!");
	}

	public static <C extends Condition<?>> Identifier getId(C condition) {
		return getIdAsResult(condition).getOrThrow(IllegalArgumentException::new);
	}

	public static Stream<Identifier> streamIds() {
		return BY_CATEGORY_AND_ID.values()
			.stream()
			.map(Map::keySet)
			.flatMap(Collection::stream);
	}

	public static <C extends Condition<?>> boolean contains(ConditionCategory<C> category, Identifier id) {
		return BY_CATEGORY_AND_ID.containsKey(category)
			&& BY_CATEGORY_AND_ID.get(category).containsKey(id);
	}

	public static <C extends Condition<?>> boolean containsId(C condition) {
		return BY_VALUES.containsKey(condition);
	}

	public static void addDirectoryPrefix(String prefix) {
		DIRECTORY_PREFIXES.add(prefix);
	}

	private static void register(ConditionEntry<?> entry) {

		Identifier id = entry.id();
		Condition<?> condition = entry.value();

		BY_VALUES.put(condition, id);
		BY_CATEGORY_AND_ID
			.computeIfAbsent(condition.getCategory(), k -> new Object2ObjectOpenHashMap<>())
			.put(id, entry);

	}

	private static void startLoading() {
		BY_CATEGORY_AND_ID.clear();
		BY_VALUES.clear();
	}

	private static void endLoading() {
		BY_CATEGORY_AND_ID.trim();
		BY_VALUES.trim();
	}

}
