package io.github.eggohito.neo_apoli.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeActionTagsS2CPacket;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeActionsS2CPacket;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.resource.JsonResourceReloader;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public final class ActionManager implements JsonResourceReloader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManager.class);
	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	public static final Identifier ID = NeoApoli.id("actions");
	public static final Set<Identifier> DEPENDENCIES = Util.make(new ObjectOpenHashSet<>(), set -> set.add(ConditionManager.ID));

	private static final Object2ObjectOpenHashMap<ActionCategory<?>, Map<Identifier, List<ActionEntry<?>>>> TAGS = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<ActionCategory<?>, Map<Identifier, ActionEntry<?>>> BY_CATEGORY_AND_ID = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<Action, Identifier> BY_VALUES = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> ops;

	public ActionManager(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.ops = wrapperLookup.getOps(JsonOps.INSTANCE);
	}

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {

		CompletableFuture<Map<ActionCategory<?>, Map<Identifier, List<TagGroupLoader.TrackedEntry>>>> preparedTagsFuture = CompletableFuture
			.supplyAsync(() -> this.prepareTags(manager, Profilers.get()), prepareExecutor);
		CompletableFuture<Map<ActionCategory<?>, Map<Identifier, Entry>>> preparedElementsFuture = CompletableFuture
			.supplyAsync(() -> this.prepareElements(manager, Profilers.get()), prepareExecutor);

		return preparedTagsFuture.thenCombine(preparedElementsFuture, Pair::of)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(
				preparedTagsAndElements -> {
					this.applyElements(preparedTagsAndElements.getSecond(), manager, Profilers.get());
					this.applyTags(preparedTagsAndElements.getFirst(), manager, Profilers.get());
				},
				applyExecutor
			);

	}

	private Map<ActionCategory<?>, Map<Identifier, List<TagGroupLoader.TrackedEntry>>> prepareTags(ResourceManager manager, Profiler profiler) {

		Map<ActionCategory<?>, Map<Identifier, List<TagGroupLoader.TrackedEntry>>> prepared = new Object2ObjectOpenHashMap<>();
		for (var category : NeoApoliRegistries.ACTION_CATEGORY) {

			String directory = RegistryKeys.getTagPath(category.registryRef());
			TagGroupLoader<ActionEntry<?>> tagLoader = new TagGroupLoader<>((id, required) -> getEntryAsResult(category, id).result(), directory);

			Map<Identifier, List<TagGroupLoader.TrackedEntry>> trackedEntries = tagLoader.loadTags(manager);

			if (!trackedEntries.isEmpty()) {
				prepared.put(category, trackedEntries);
			}

		}

		return prepared;

	}

	private void applyTags(Map<ActionCategory<?>, Map<Identifier, List<TagGroupLoader.TrackedEntry>>> prepared, ResourceManager manager, Profiler profiler) {

		LOGGER.info("Parsing action tags from data packs...");
		TAGS.clear();

		prepared.forEach((category, entries) -> {

			String directory = RegistryKeys.getPath(category.registryRef());
			TagGroupLoader<ActionEntry<?>> tagLoader = new TagGroupLoader<>((id, required) -> getEntryAsResult(category, id).result(), directory);

			TAGS.put(category, tagLoader.buildGroup(entries));

		});

		StringBuilder message = new StringBuilder("Finished parsing action tags from data packs. Parsed " + TAGS.size() + " action tag(s) in total;");
		TAGS.forEach((category, entries) -> message.append("\n\t - Parsed ").append(entries.size()).append(" ").append(StringUtils.uncapitalize(category.toString())).append(" tag(s)"));

		LOGGER.info(message.toString());
		TAGS.trim();

	}

	private Map<ActionCategory<?>, Map<Identifier, Entry>> prepareElements(ResourceManager manager, Profiler profiler) {

		Map<ActionCategory<?>, Map<Identifier, Entry>> prepared = new Object2ObjectOpenHashMap<>();
		for (var category : NeoApoliRegistries.ACTION_CATEGORY) {

			String directory = RegistryKeys.getPath(category.registryRef());
			manager.findResources(directory, this::supportsJsonFormat).forEach((fileId, resource) -> {

				String packName = resource.getPackId();
				Identifier resourceId = this.trimExtension(fileId, directory);

				try (BufferedReader resourceReader = resource.getReader()) {

					GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, this.getJsonFormat(fileId)));
					JsonElement jsonElement = GSON.fromJson(gsonReader, JsonElement.class);

					if (jsonElement != null) {

						if (MiscUtil.isResourceConditionFulfilled(resourceId, jsonElement, directory, ops)) {

							Entry entry = new Entry() {

								@Override
								public String source() {
									return packName;
								}

								@Override
								public JsonElement element() {
									return jsonElement;
								}

							};

							prepared
								.computeIfAbsent(category, k -> new Object2ObjectOpenHashMap<>())
								.put(resourceId, entry);

						}

					}

					else {
						throw new JsonSyntaxException("JSON file cannot be empty!");
					}

				}

				catch (Exception e) {
					LOGGER.error("Error trying to prepare {} JSON file \"{}\" from data pack [{}] (skipping): {}", StringUtils.uncapitalize(category.toString()), fileId, packName, e);
				}

			});

		}

		return prepared;

	}

	private void applyElements(Map<ActionCategory<?>, Map<Identifier, Entry>> prepared, ResourceManager manager, Profiler profiler) {

		LOGGER.info("Parsing actions from data packs...");
		startLoading();

		prepared.forEach((category, entries) -> entries.forEach((id, entry) -> category.codec().parse(ops, entry.element())
			.ifSuccess(action -> register(id, action))
			.ifError(error -> LOGGER.error("Error trying to parse {} \"{}\" from data pack [{}] (skipping): {}", StringUtils.uncapitalize(category.toString()), id, entry.source(), error.message()))));

		StringBuilder message = new StringBuilder("Finished parsing actions from data packs. Parsed " + BY_CATEGORY_AND_ID.size() + " action(s) in total;");
		BY_CATEGORY_AND_ID.forEach((category, entries) -> message.append("\n\t - Parsed ").append(entries.size()).append(" ").append(StringUtils.uncapitalize(category.toString())).append("(s)"));

		LOGGER.info(message.toString());
		endLoading();

	}

	@ApiStatus.Internal
	public static void init() {

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, ActionManager::new);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ConditionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));

	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return DEPENDENCIES;
	}

	@ApiStatus.Internal
	public static void sendSyncPayload(ServerPlayerEntity player) {

		if (!player.server.isRemote()) {
			return;
		}

		Map<ActionCategory<?>, Map<Identifier, Action>> filteredEntries = new Object2ObjectOpenHashMap<>();
		BY_CATEGORY_AND_ID.forEach((category, entries) -> entries.forEach((id, entry) -> filteredEntries
			.computeIfAbsent(category, k -> new Object2ObjectOpenHashMap<>())
			.put(id, entry.value())));

		LOGGER.info("Sent {} action(s) to player {}!", filteredEntries.size(), player.getName().getString());
		ServerPlayNetworking.send(player, new SynchronizeActionsS2CPacket(filteredEntries));

	}

	@ApiStatus.Internal
	public static void sendTagSyncPayload(ServerPlayerEntity player) {

		if (!player.server.isRemote()) {
			return;
		}

		LOGGER.info("Sent {} action tag(s) to player {}!", TAGS.size(), player.getName().getString());
		ServerPlayNetworking.send(player, new SynchronizeActionTagsS2CPacket(TAGS));

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveSyncPayload(SynchronizeActionsS2CPacket payload, ClientPlayNetworking.Context context) {

		Objects.requireNonNull(context.client(), "client");
		Objects.requireNonNull(context.responseSender(), "responseSender");

		startLoading();
		payload.actions().forEach((category, entries) -> entries.forEach(ActionManager::register));
		endLoading();

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveSyncTagPayload(SynchronizeActionTagsS2CPacket payload, ClientPlayNetworking.Context context) {

		Objects.requireNonNull(context.client(), "client");
		Objects.requireNonNull(context.responseSender(), "responseSender");

		TAGS.clear();
		TAGS.putAll(payload.actionTags());
		TAGS.trim();

	}

	public static <A extends Action> List<ActionEntry<?>> getEntriesFromTagOrEmpty(ActionCategory<A> category, TagKey<A> tag) {
		return TAGS.getOrDefault(category, new Object2ObjectOpenHashMap<>()).getOrDefault(tag.id(), new ObjectArrayList<>());
	}

	public static <A extends Action> List<ActionEntry<?>> getEntriesFromTagOrEmpty(ActionCategory<A> category, Identifier tagId) {
		return getEntriesFromTagOrEmpty(category, TagKey.of(category.registryRef(), tagId));
	}

	@SuppressWarnings("unchecked")
	public static <A extends Action> DataResult<ActionEntry<A>> getEntryAsResult(ActionCategory<A> category, Identifier id) {

		Map<Identifier, ActionEntry<?>> entries = BY_CATEGORY_AND_ID.getOrDefault(category, new Object2ObjectOpenHashMap<>());
		ActionEntry<?> entry = entries.get(id);

		if (entry != null) {
			return DataResult.success((ActionEntry<A>) entry);
		}

		else {
			return DataResult.error(() -> category + " with ID \"" + id + "\" does not exist!");
		}

	}

	public static <A extends Action> ActionEntry<A> getEntry(ActionCategory<A> category, Identifier id) {
		return getEntryAsResult(category, id).getOrThrow(IllegalArgumentException::new);
	}

	public static <A extends Action> DataResult<A> getAsResult(ActionCategory<A> category, Identifier id) {
		return getEntryAsResult(category, id).map(ActionEntry::value);
	}

	public static <A extends Action> A get(ActionCategory<A> category, Identifier id) {
		return getAsResult(category, id).getOrThrow(IllegalArgumentException::new);
	}

	public static <A extends Action> DataResult<Identifier> getIdAsResult(A action) {
		return containsId(action)
			? DataResult.success(BY_VALUES.get(action))
			: DataResult.error(() -> action + " doesn't correspond to any identifiers!");
	}

	public static <A extends Action> Identifier getId(A action) {
		return getIdAsResult(action).getOrThrow(IllegalArgumentException::new);
	}

	public static Stream<Identifier> streamIds(ActionCategory<?> category) {
		return BY_CATEGORY_AND_ID.getOrDefault(category, new Object2ObjectOpenHashMap<>())
			.keySet()
			.stream();
	}

	public static Stream<Identifier> streamIds() {
		return BY_CATEGORY_AND_ID.values()
			.stream()
			.map(Map::keySet)
			.flatMap(Collection::stream);
	}

	public static <A extends Action> boolean contains(ActionCategory<A> category, Identifier id) {
		return BY_CATEGORY_AND_ID.containsKey(category)
			&& BY_CATEGORY_AND_ID.get(category).containsKey(id);
	}

	public static <A extends Action> boolean containsId(A action) {
		return BY_VALUES.containsKey(action);
	}

	public static SuggestionProvider<ServerCommandSource> createSuggestionProvider(ActionCategory<?> category) {
		return (context, builder) -> CommandSource.suggestIdentifiers(streamIds(category), builder);
	}

	private static void register(Identifier id, Action action) {
		BY_VALUES.put(action, id);
		BY_CATEGORY_AND_ID
			.computeIfAbsent(action.getCategory(), k -> new Object2ObjectOpenHashMap<>())
			.put(id, new ActionEntry<>(id, action));
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
