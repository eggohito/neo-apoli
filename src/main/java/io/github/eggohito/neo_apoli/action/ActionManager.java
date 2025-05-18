package io.github.eggohito.neo_apoli.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
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

public final class ActionManager extends SinglePreparationResourceReloader<Map<Identifier, Pair<ActionCategory<?>, IMultiDirectoryResourceReloader.Entry>>> implements IMultiDirectoryResourceReloader {

	private static final Set<String> DIRECTORY_PREFIXES = new ObjectOpenHashSet<>();

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManager.class);
	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	public static final Identifier ID = NeoApoli.id("block_actions");
	public static final Set<Identifier> DEPENDENCIES = new ObjectOpenHashSet<>();

	private static final Object2ObjectOpenHashMap<ActionCategory<?>, Map<Identifier, ActionEntry<?>>> ACTIONS_BY_ID = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<Action<?>, Identifier> ID_BY_ACTIONS = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> ops;

	public ActionManager(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.ops = wrapperLookup.getOps(JsonOps.INSTANCE);
	}

	@ApiStatus.Internal
	public static void init() {

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, ActionManager::new);
		addDirectoryPrefix(NeoApoli.MOD_NAMESPACE);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));

	}

	@Override
	protected Map<Identifier, Pair<ActionCategory<?>, Entry>> prepare(ResourceManager manager, Profiler profiler) {

		Map<Identifier, Pair<ActionCategory<?>, Entry>> prepared = new Object2ObjectOpenHashMap<>();
		Map<ActionCategory<?>, Identifier> processed = new Object2ObjectOpenHashMap<>();

		NeoApoliRegistries.ACTION_CATEGORY.forEach(category -> {

			String simpleClassName = this.getClass().getSimpleName();
			Set<String> directories = this.getDirectories(category);

			for (String directory : directories) {

				Map<Identifier, Resource> resources = manager.findResources(directory, this::supportsJsonFormat);
				profiler.push("[" + simpleClassName + "] scanning files in directory \"" + directory + "\" from data packs");

				for (Map.Entry<Identifier, Resource> resourceEntry : resources.entrySet()) {

					Identifier fileId = resourceEntry.getKey();
					String fileExtension = "." + FilenameUtils.getExtension(fileId.getPath());

					Identifier resourceId = this.trimExtension(fileId, directory);
					Resource resource = resourceEntry.getValue();

					JsonFormat jsonFormat = this.getSupportedJsonFormats().get(fileExtension);
					String packName = resource.getPackId();

					profiler.push("[" + simpleClassName + "] preparing file \"" + fileId + "\" from data pack {" + packName + "}");

					if (processed.containsKey(category) && processed.get(category).equals(resourceId)) {
						LOGGER.warn("Ignored duplicate JSON file with ID \"{}\" from directory \"{}\" of data pack [{}]!", resourceId, directory, packName);
					}

					else {

						try (BufferedReader reader = resource.getReader()) {

							GsonReader gsonReader = new GsonReader(JsonReader.create(reader, jsonFormat));
							JsonElement jsonElement = GSON.fromJson(gsonReader, JsonElement.class);

							if (jsonElement == null) {
								throw new JsonParseException("JSON cannot be empty!");
							}

							else {
								prepared.put(resourceId, Pair.of(category, new Entry(packName, jsonElement)));
								processed.put(category, resourceId);
							}

						}

						catch (Exception e) {
							LOGGER.error("Error trying to prepare JSON for file \"{}\" from data pack [{}] (skipping): {}", fileId, packName, e);
						}

					}

				}

			}

		});

		return prepared;

	}

	@Override
	protected void apply(Map<Identifier, Pair<ActionCategory<?>, Entry>> prepared, ResourceManager manager, Profiler profiler) {

		String simpleClassName = this.getClass().getSimpleName();
		profiler.push("[" + simpleClassName + "] start parsing actions");

		NeoApoli.LOGGER.info("Parsing actions from data packs...");
		startLoading();

		prepared.forEach((id, pair) -> {

			ActionCategory<?> category = pair.getFirst();
			Entry entry = pair.getSecond();

			String actionName = StringUtils.uncapitalize(category.toString());
			profiler.push("[" + simpleClassName + "] parsing " + actionName + "\"" + id + "\" from data pack {" + entry.source() + "}");

			try {
				register(new ActionEntry<>(id, category.codec().parse(ops, entry.element()).getOrThrow()));
			}

			catch (Exception e) {
				NeoApoli.LOGGER.error("Error trying to parse {} \"{}\" from data pack [{}] (skipping): {}", actionName, id, entry.source(), e);
			}

		});

		profiler.pop();

		NeoApoli.LOGGER.info("Finished loading actions from data packs. Parsed {} action(s) in total;", ACTIONS_BY_ID.size());
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

		Set<ActionEntry<?>> entries = ACTIONS_BY_ID.values()
			.stream()
			.map(Map::values)
			.flatMap(Collection::stream)
			.collect(Collectors.toCollection(ObjectOpenHashSet::new));

		NeoApoli.LOGGER.info("Sent {} action(s) to player {}!", ACTIONS_BY_ID.size(), player.getName().getString());
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

		if (ACTIONS_BY_ID.containsKey(category)) {
			ActionEntry<?> entry = ACTIONS_BY_ID.get(category).get(id);
			return entry != null
				? DataResult.success((ActionEntry<A>) entry)
				: DataResult.error(() -> category.toString() + " with ID \"" + id + "\" does not exist!");
		}

		else {
			return DataResult.error(() -> "No " + StringUtils.uncapitalize(category.toString()) + "s are registered!");
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
			? DataResult.success(ID_BY_ACTIONS.get(action))
			: DataResult.error(() -> action.getCategory() + " " + action + " doesn't correspond to any identifiers!");
	}

	public static <A extends Action<?>> Identifier getId(A action) {
		return getIdAsResult(action).getOrThrow(IllegalArgumentException::new);
	}

	public static Stream<Identifier> streamIds() {
		return ACTIONS_BY_ID.values()
			.stream()
			.map(Map::keySet)
			.flatMap(Collection::stream);
	}

	public static <A extends Action<?>> boolean contains(ActionCategory<A> category, Identifier id) {
		return ACTIONS_BY_ID.containsKey(category)
			&& ACTIONS_BY_ID.get(category).containsKey(id);
	}

	public static <A extends Action<?>> boolean containsId(A action) {
		return ID_BY_ACTIONS.containsKey(action);
	}

	public static void addDirectoryPrefix(String prefix) {
		DIRECTORY_PREFIXES.add(prefix);
	}

	private static void register(ActionEntry<?> entry) {

		Identifier id = entry.id();
		Action<?> action = entry.value();

		ID_BY_ACTIONS.put(action, id);
		ACTIONS_BY_ID
			.computeIfAbsent(action.getCategory(), k -> new Object2ObjectOpenHashMap<>())
			.put(id, entry);

	}

	private static void startLoading() {
		ACTIONS_BY_ID.clear();
		ID_BY_ACTIONS.clear();
	}

	private static void endLoading() {
		ACTIONS_BY_ID.trim();
		ID_BY_ACTIONS.trim();
	}

}
