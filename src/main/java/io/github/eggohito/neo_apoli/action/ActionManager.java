package io.github.eggohito.neo_apoli.action;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.api.event.ReloadableServerResourcesEvents;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizeActionTagsS2CPacket;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizeActionsS2CPacket;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonElementWithSource;
import io.github.eggohito.neo_apoli.resource.json.JsonReloadListener;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public final class ActionManager implements JsonReloadListener {

	public static final ResourceLocation ID = NeoApoli.id("manager/actions");
	public static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.ACTIONS.invoker()::add).build();

	public static final TagEntry.Lookup<Action> TAG_LOOKUP =  new TagEntry.Lookup<>() {

		@Nullable
		@Override
		public Action element(ResourceLocation id, boolean required) {
			return getAsResult(id).result().orElse(null);
		}

		@Nullable
		@Override
		public Collection<Action> tag(ResourceLocation id) {
			return getEntriesFromTag(id).result().orElse(null);
		}

		@Override
		public String toString() {
			return "Action manager";
		}

	};

	private static final String TAG_DIRECTORY = Registries.tagsDirPath(NeoApoliRegistryKeys.ACTION);
	private static final String DIRECTORY = Registries.elementsDirPath(NeoApoliRegistryKeys.ACTION);

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManager.class);
	private static final TagLoader<Action> TAG_LOADER = new TagLoader<>((id, required) -> getAsResult(id).result(), TAG_DIRECTORY);

	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	private static final Object2ObjectOpenHashMap<ResourceLocation, Action> BY_ID = new Object2ObjectOpenHashMap<>();
	private static final IdentityHashMap<Action, ResourceLocation> BY_ACTION = new IdentityHashMap<>();

	private static final Object2ObjectOpenHashMap<ResourceLocation, List<TagLoader.EntryWithSource>> PREPARED_TAGS = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<ResourceLocation, List<Action>> TAGS = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> ops;

	ActionManager(HolderLookup.Provider wrapperLookup) {
		this.ops = wrapperLookup.createSerializationContext(JsonOps.INSTANCE);
	}

	@Override
	public CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {

		CompletableFuture<Map<ResourceLocation, JsonElementWithSource>> preparedElementsFuture = CompletableFuture
			.supplyAsync(() -> prepareElements(manager, Profiler.get()), prepareExecutor);
		CompletableFuture<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> preparedTagsFuture = CompletableFuture
			.supplyAsync(() -> preparePendingTags(manager, Profiler.get()), prepareExecutor);

		return preparedTagsFuture.thenCombine(preparedElementsFuture, Pair::of)
			.thenCompose(synchronizer::wait)
			.thenAcceptAsync(preparedTagsAndElements -> this.applyElements(preparedTagsAndElements.getSecond(), manager, Profiler.get()), applyExecutor);

	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}

	private Map<ResourceLocation, List<TagLoader.EntryWithSource>> preparePendingTags(ResourceManager manager, ProfilerFiller ignoredProfiler) {

		PREPARED_TAGS.clear();
		Map<ResourceLocation, List<TagLoader.EntryWithSource>> pendingTags = TAG_LOADER.load(manager);

		PREPARED_TAGS.putAll(pendingTags);
		PREPARED_TAGS.trim();

		return PREPARED_TAGS;

	}

	private Map<ResourceLocation, JsonElementWithSource> prepareElements(ResourceManager manager, ProfilerFiller ignoredProfiler) {

		Map<ResourceLocation, JsonElementWithSource> prepared = new Object2ObjectOpenHashMap<>();
		manager.listResources(DIRECTORY, this::supportsFormat).forEach((fileId, resource) -> {

			String packId = resource.sourcePackId();
			ResourceLocation resourceId = this.trimExtension(fileId, DIRECTORY);

			try (BufferedReader resourceReader = resource.openAsReader()) {

				JsonFormat jsonFormat = this.getFormat(fileId);
				GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, jsonFormat));

				JsonElement jsonElement = GSON.fromJson(gsonReader, JsonElement.class);

				switch (jsonElement) {
					case JsonElement asIs when MiscUtil.isResourceConditionFulfilled(resourceId, asIs, DIRECTORY, ops) -> {

						var newElement = JsonElementWithSource.of(packId, asIs, jsonFormat);
						var oldElement = prepared.get(resourceId);

						if (oldElement != null) {
							throw new IllegalStateException("Duplicate of an action JSON with the same name but a different file extension! (extension: " + oldElement.format().name().toLowerCase(Locale.ROOT) + ")");
						}

						else {
							prepared.put(resourceId, newElement);
						}

					}
					case null ->
						throw new JsonParseException("JSON file cannot be empty!");
					default -> {
						//	No-op
					}
				}

			}

			catch (Exception e) {
				LOGGER.error("Error trying to prepare action JSON file \"{}\" from data pack [{}] (skipping): {}", fileId, packId, e);
			}

		});

		return prepared;

	}

	private static void applyPendingTags(ReloadableServerResources ignored) {

		if (PREPARED_TAGS.isEmpty()) {
			return;
		}

		LOGGER.info("Parsing action tags from data packs...");
		TAGS.clear();

		TAGS.putAll(TAG_LOADER.build(PREPARED_TAGS));
		LOGGER.info("Finished parsing action tags from data packs. Parsed {} action tag(s)", TAGS.size());

		PREPARED_TAGS.clear();
		TAGS.trim();

	}

	private void applyElements(Map<ResourceLocation, JsonElementWithSource> prepared, ResourceManager ignoredManager, ProfilerFiller ignoredProfiler) {

		LOGGER.info("Parsing actions from data packs...");
		BY_ID.clear();

		prepared.forEach((id, entry) -> {

			ResourceLocationUtil.setCurrent(id);
			Action.CODEC.parse(ops, entry.element())
				.ifSuccess(action -> register(id, action))
				.ifError(error -> LOGGER.error("Error trying to parse action \"{}\" from data pack [{}] (skipping): {}", id, entry.source(), error.message()));

			ResourceLocationUtil.setCurrent(null);

		});

		LOGGER.info("Finished parsing actions from data packs. Parsed {} action(s)", BY_ID.size());

	}

	@ApiStatus.Internal
	public static void sendSyncPayload(ServerPlayer receiver) {

		if (!receiver.server.isPublished()) {
			return;
		}

		LOGGER.info("Sent {} action(s) to player {}!", BY_ID.size(), receiver.getName().getString());
		ServerPlayNetworking.send(receiver, new SynchronizeActionsS2CPacket(BY_ID));

	}

	@ApiStatus.Internal
	public static void receiveSyncPayload(SynchronizeActionsS2CPacket payload) {

		BY_ID.clear();
		BY_ACTION.clear();

		payload.actions().forEach(ActionManager::register);

		BY_ID.trim();

	}

	@ApiStatus.Internal
	public static void sendTagSyncPayload(ServerPlayer receiver) {

		if (!receiver.server.isPublished()) {
			return;
		}

		LOGGER.info("Sent {} action tag(s) to player {}!", TAGS.size(), receiver.getName().getString());
		ServerPlayNetworking.send(receiver, new SynchronizeActionTagsS2CPacket(TAGS));

	}

	@ApiStatus.Internal
	public static void receiveTagSyncPayload(SynchronizeActionTagsS2CPacket payload) {

		TAGS.clear();
		TAGS.putAll(payload.tags());

		TAGS.trim();

	}

	private static void register(ResourceLocation id, Action action) {
		BY_ID.put(id, action);
		BY_ACTION.put(action, id);
	}

	public static DataResult<Action> getAsResult(ResourceLocation id) {
		return contains(id)
			? DataResult.success(BY_ID.get(id))
			: DataResult.error(() -> "Action with ID \"" + id + "\" does not exist!");
	}

	public static Action get(ResourceLocation id) {
		return getAsResult(id).getOrThrow();
	}

	public static DataResult<ResourceLocation> getIdAsResult(Action action) {
		return containsId(action)
			? DataResult.success(BY_ACTION.get(action))
			: DataResult.error(() -> action + " doesn't correspond to any identifiers!");
	}

	public static ResourceLocation getId(Action action) {
		return getIdAsResult(action).getOrThrow();
	}

	public static DataResult<List<Action>> getEntriesFromTag(TagKey<Action> tag) {
		return getEntriesFromTag(tag.location());
	}

	public static DataResult<List<Action>> getEntriesFromTag(ResourceLocation tagId) {
		return Optional.ofNullable(TAGS.get(tagId))
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error(() -> "Unknown action tag: " + tagId));
	}

	public static Stream<Action> actions() {
		return BY_ID.values().stream();
	}

	public static Stream<ResourceLocation> ids() {
		return BY_ID.keySet().stream();
	}

	public static boolean contains(ResourceLocation id) {
		return BY_ID.containsKey(id);
	}

	public static boolean containsId(Action action) {
		return BY_ACTION.containsKey(action);
	}

	public static ValueSuppliedElementCodec<Action> createEntryCodec(boolean allowInlineDefinitions) {
		return new ValueSuppliedElementCodec<>(Action.CODEC, allowInlineDefinitions, ActionManager::getAsResult, ActionManager::getIdAsResult);
	}

	public static void init() {

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, ActionManager::new);
		DependencyManager.ACTIONS.register(ID, dependencies -> dependencies.add(ConditionManager.ID));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ConditionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));

		ReloadableServerResourcesEvents.RegistryTagUpdate.AFTER.register(ID, ActionManager::applyPendingTags);

	}

}
