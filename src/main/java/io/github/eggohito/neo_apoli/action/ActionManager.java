package io.github.eggohito.neo_apoli.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeActionsS2CPacket;
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
import net.minecraft.util.Util;
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

public final class ActionManager extends SinglePreparationResourceReloader<Map<ActionCategory<?>, Map<Identifier, IMultiDirectoryResourceReloader.Entry>>> implements IMultiDirectoryResourceReloader {

	private static final Set<String> DIRECTORY_PREFIXES = new ObjectOpenHashSet<>();

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManager.class);
	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	public static final Identifier ID = NeoApoli.id("actions");
	public static final Set<Identifier> DEPENDENCIES = Util.make(new ObjectOpenHashSet<>(), set -> set.add(ConditionManager.ID));

	private static final Object2ObjectOpenHashMap<ActionCategory<?>, Map<Identifier, ActionEntry<?>>> BY_CATEGORY_AND_ID = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<Action<?>, Identifier> BY_VALUES = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> ops;

	public ActionManager(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.ops = wrapperLookup.getOps(JsonOps.INSTANCE);
	}

	@ApiStatus.Internal
	public static void init() {

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, ActionManager::new);
		addDirectoryPrefix(NeoApoli.MOD_NAMESPACE);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ConditionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));

	}

	@Override
	protected Map<ActionCategory<?>, Map<Identifier, Entry>> prepare(ResourceManager manager, Profiler profiler) {

		Map<ActionCategory<?>, Map<Identifier, Entry>> prepared = new Object2ObjectOpenHashMap<>();

		for (ActionCategory<?> category : NeoApoliRegistries.ACTION_CATEGORY) {

			Set<String> directories = this.getDirectories(category);

			for (String directory : directories) {

				Map<Identifier, Resource> resources = manager.findResources(directory, this::supportsJsonFormat);
				String uncapitalizedCategory = StringUtils.uncapitalize(category.toString());

				profiler.push("[" + ActionManager.class.getSimpleName() + "] scanning " + uncapitalizedCategory + " files in directory \"" + directory + "\" from data packs");

				for (Map.Entry<Identifier, Resource> resourceEntry : resources.entrySet()) {

					Identifier fileId = resourceEntry.getKey();
					String fileExtension = "." + FilenameUtils.getExtension(fileId.getPath());

					Identifier resourceId = this.trimExtension(fileId, directory);
					Resource resource = resourceEntry.getValue();

					JsonFormat jsonFormat = this.getSupportedJsonFormats().get(fileExtension);
					String packName = resource.getPackId();

					profiler.push("[" + ActionManager.class.getSimpleName() + "] preparing " + uncapitalizedCategory + " file \"" + fileId + "\" from data pack {" + packName + "}");

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
	protected void apply(Map<ActionCategory<?>, Map<Identifier, Entry>> prepared, ResourceManager manager, Profiler profiler) {

		LOGGER.info("Parsing actions from data packs...");
		profiler.push("[" + ActionManager.class.getSimpleName() + "] start parsing actions");

		startLoading();
		prepared.forEach((category, entries) -> entries.forEach((id, entry) -> {

			String uncapitalizedCategoryName = StringUtils.uncapitalize(category.toString());
			profiler.push("[" + ActionManager.class.getSimpleName() + "] parsing " + uncapitalizedCategoryName + " JSON \"" + id + "\" from data pack {" + entry.source() + "}");

			try {
				register(new ActionEntry<>(id, category.codec().parse(ops, entry.element()).getOrThrow()));
			}

			catch (Exception e) {
				LOGGER.error("Error trying to parse {} \"{}\" from data pack [{}] (skipping): {}", uncapitalizedCategoryName, id, entry.source(), e.getMessage());
			}

			profiler.pop();

		}));

		profiler.pop();

		StringBuilder messageBuilder = new StringBuilder("Finished parsing actions from data packs. Parsed " + BY_CATEGORY_AND_ID.size() + " action(s) in total;");
		BY_CATEGORY_AND_ID.forEach((category, entries) -> messageBuilder.append("\n\t - Parsed ").append(entries.size()).append(" ").append(StringUtils.uncapitalize(category.toString())).append("(s)"));

		LOGGER.info(messageBuilder.toString());
		endLoading();

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
		NeoApoliRegistries.ACTION_CATEGORY.forEach(category -> directories.addAll(this.getDirectories(category)));

		return directories;

	}

	public Set<String> getDirectories(ActionCategory<?> category) {

		Set<String> directories = new ObjectOpenHashSet<>();
		String directory = category.directory();

		for (String prefix : DIRECTORY_PREFIXES) {
			directories.add(prefix + "/" + directory);
		}

		directories.add(directory);
		return directories;

	}

	@ApiStatus.Internal
	public static void sendSyncPayload(ServerPlayerEntity player) {

		if (!player.server.isRemote()) {
			return;
		}

		Set<ActionEntry<?>> entries = BY_CATEGORY_AND_ID.values()
			.stream()
			.map(Map::values)
			.flatMap(Collection::stream)
			.collect(Collectors.toCollection(ObjectOpenHashSet::new));

		LOGGER.info("Sent {} action(s) to player {}!", entries.size(), player.getName().getString());
		ServerPlayNetworking.send(player, new SynchronizeActionsS2CPacket(entries));

	}

	@ApiStatus.Internal
	public static void receiveSyncPayload(SynchronizeActionsS2CPacket payload) {
		startLoading();
		payload.actions().forEach(ActionManager::register);
		endLoading();
	}

	@SuppressWarnings("unchecked")
	public static <A extends Action<?>> DataResult<ActionEntry<A>> getEntryAsResult(ActionCategory<A> category, Identifier id) {

		Map<Identifier, ActionEntry<?>> entries = BY_CATEGORY_AND_ID.getOrDefault(category, new Object2ObjectOpenHashMap<>());
		ActionEntry<?> entry = entries.get(id);

		if (entry != null) {
			return DataResult.success((ActionEntry<A>) entry);
		}

		else {
			return DataResult.error(() -> category + " with ID \"" + id + "\" does not exist!");
		}

	}

	public static <A extends Action<?>> ActionEntry<A> getEntry(ActionCategory<A> category, Identifier id) {
		return getEntryAsResult(category, id).getOrThrow(IllegalArgumentException::new);
	}

	public static <A extends Action<?>> DataResult<A> getAsResult(ActionCategory<A> category, Identifier id) {
		return getEntryAsResult(category, id).map(ActionEntry::value);
	}

	public static <A extends Action<?>> A get(ActionCategory<A> category, Identifier id) {
		return getAsResult(category, id).getOrThrow(IllegalArgumentException::new);
	}

	public static <A extends Action<?>> DataResult<Identifier> getIdAsResult(A action) {
		return containsId(action)
			? DataResult.success(BY_VALUES.get(action))
			: DataResult.error(() -> action.asDisplayString(true) + " doesn't correspond to any identifiers!");
	}

	public static <A extends Action<?>> Identifier getId(A action) {
		return getIdAsResult(action).getOrThrow(IllegalArgumentException::new);
	}

	public static Stream<Identifier> streamIds() {
		return BY_CATEGORY_AND_ID.values()
			.stream()
			.map(Map::keySet)
			.flatMap(Collection::stream);
	}

	public static <A extends Action<?>> boolean contains(ActionCategory<A> category, Identifier id) {
		return BY_CATEGORY_AND_ID.containsKey(category)
			&& BY_CATEGORY_AND_ID.get(category).containsKey(id);
	}

	public static <A extends Action<?>> boolean containsId(A action) {
		return BY_VALUES.containsKey(action);
	}

	public static void addDirectoryPrefix(String prefix) {
		DIRECTORY_PREFIXES.add(prefix);
	}

	private static void register(ActionEntry<?> entry) {

		Identifier id = entry.id();
		Action<?> action = entry.value();

		BY_VALUES.put(action, id);
		BY_CATEGORY_AND_ID
			.computeIfAbsent(action.getCategory(), k -> new Object2ObjectOpenHashMap<>())
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
