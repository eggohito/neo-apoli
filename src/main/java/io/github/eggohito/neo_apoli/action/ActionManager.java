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
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.integration.DependencyManager;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeActionTagsS2CPacket;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeActionsS2CPacket;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.JsonResourceReloader;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
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

	private static final String TAG_DIRECTORY = RegistryKeys.getTagPath(NeoApoliRegistryKeys.ACTION);
	private static final String DIRECTORY = RegistryKeys.getPath(NeoApoliRegistryKeys.ACTION);

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManager.class);
	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	public static final Identifier ID = NeoApoli.id("manager/actions");
	public static final ImmutableSet<Identifier> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.ACTIONS.invoker()::add).build();

	private static final Object2ObjectOpenHashMap<Identifier, Action> BY_ID = new Object2ObjectOpenHashMap<>();
	private static final IdentityHashMap<Action, Identifier> BY_ACTION = new IdentityHashMap<>();

	private static final TagGroupLoader<Action> TAG_LOADER = new TagGroupLoader<>((id, required) -> getAsResult(id).result(), TAG_DIRECTORY);
	private static final Object2ObjectOpenHashMap<Identifier, List<Action>> TAGS = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> ops;

	ActionManager(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.ops = wrapperLookup.getOps(JsonOps.INSTANCE);
	}

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {

		CompletableFuture<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> preparedTagsFuture = CompletableFuture
			.supplyAsync(() -> prepareTags(manager, Profilers.get()), prepareExecutor);
		CompletableFuture<Map<Identifier, Entry>> preparedElementsFuture = CompletableFuture
			.supplyAsync(() -> prepareElements(manager, Profilers.get()), prepareExecutor);

		return preparedTagsFuture.thenCombine(preparedElementsFuture, Pair::of)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(
				pair -> {
					applyTags(pair.getFirst(), manager, Profilers.get());
					applyElements(pair.getSecond(), manager, Profilers.get());
				},
				applyExecutor
			);

	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return DEPENDENCIES;
	}

	private Map<Identifier, List<TagGroupLoader.TrackedEntry>> prepareTags(ResourceManager manager, Profiler ignoredProfiler) {
		return TAG_LOADER.loadTags(manager);
	}

	private Map<Identifier, Entry> prepareElements(ResourceManager manager, Profiler ignoredProfiler) {

		Map<Identifier, Entry> prepared = new Object2ObjectOpenHashMap<>();
		manager.findResources(DIRECTORY, this::supportsJsonFormat).forEach((fileId, resource) -> {

			String packId = resource.getPackId();
			Identifier resourceId = this.trimExtension(fileId, DIRECTORY);

			try (BufferedReader resourceReader = resource.getReader()) {

				GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, this.getJsonFormat(fileId)));
				JsonElement jsonElement = GSON.fromJson(gsonReader, JsonElement.class);

				switch (jsonElement) {
					case JsonElement asIs when MiscUtil.isResourceConditionFulfilled(resourceId, asIs, DIRECTORY, ops) ->
						prepared.put(resourceId, new Entry() {

							@Override
							public String source() {
								return packId;
							}

							@Override
							public JsonElement element() {
								return asIs;
							}

						});
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

	private void applyTags(Map<Identifier, List<TagGroupLoader.TrackedEntry>> prepared, ResourceManager ignoredManager, Profiler ignoredProfiler) {

		LOGGER.info("Parsing action tags from data packs...");
		TAGS.clear();

		TAGS.putAll(TAG_LOADER.buildGroup(prepared));
		LOGGER.info("Finished parsing action tags from data packs. Parsed {} action tag(s)", TAGS.size());

		TAGS.trim();

	}

	private void applyElements(Map<Identifier, Entry> prepared, ResourceManager ignoredManager, Profiler ignoredProfiler) {

		LOGGER.info("Parsing actions from data packs...");
		BY_ID.clear();

		prepared.forEach((id, entry) -> Action.CODEC.parse(ops, entry.element())
			.ifSuccess(action -> BY_ID.put(id, action))
			.ifError(error -> LOGGER.error("Error trying to parse action \"{}\" from data pack [{}] (skipping): {}", id, entry.source(), error.message())));

		LOGGER.info("Finished parsing actions from data packs. Parsed {} action(s)", BY_ID.size());

	}

	@ApiStatus.Internal
	public static void sendSyncPayload(ServerPlayerEntity receiver) {

		if (!receiver.server.isRemote()) {
			return;
		}

		LOGGER.info("Sent {} action(s) to player {}!", BY_ID.size(), receiver.getName().getString());
		ServerPlayNetworking.send(receiver, new SynchronizeActionsS2CPacket(BY_ID));

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveSyncPayload(SynchronizeActionsS2CPacket payload, ClientPlayNetworking.Context context) {

		Objects.requireNonNull(context.client(), "client");
		Objects.requireNonNull(context.responseSender(), "responseSender");

		BY_ID.clear();
		BY_ACTION.clear();

		payload.actions().forEach(ActionManager::register);

		BY_ID.trim();

	}

	@ApiStatus.Internal
	public static void sendTagSyncPayload(ServerPlayerEntity receiver) {

		if (!receiver.server.isRemote()) {
			return;
		}

		LOGGER.info("Sent {} action tag(s) to player {}!", TAGS.size(), receiver.getName().getString());
		ServerPlayNetworking.send(receiver, new SynchronizeActionTagsS2CPacket(TAGS));

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveTagSyncPayload(SynchronizeActionTagsS2CPacket payload, ClientPlayNetworking.Context context) {

		Objects.requireNonNull(context.client(), "client");
		Objects.requireNonNull(context.responseSender(), "responseSender");

		TAGS.clear();
		TAGS.putAll(payload.tags());

		TAGS.trim();

	}

	private static void register(Identifier id, Action action) {
		BY_ID.put(id, action);
		BY_ACTION.put(action, id);
	}

	public static DataResult<Action> getAsResult(Identifier id) {
		return contains(id)
			? DataResult.success(BY_ID.get(id))
			: DataResult.error(() -> "Action with ID \"" + id + "\" does not exist!");
	}

	public static Action get(Identifier id) {
		return getAsResult(id).getOrThrow();
	}

	public static DataResult<Identifier> getIdAsResult(Action action) {
		return containsId(action)
			? DataResult.success(BY_ACTION.get(action))
			: DataResult.error(() -> action + " doesn't correspond to any identifiers!");
	}

	public static Identifier getId(Action action) {
		return getIdAsResult(action).getOrThrow();
	}

	public static Stream<Action> actions() {
		return BY_ID.values().stream();
	}

	public static Stream<Identifier> ids() {
		return BY_ID.keySet().stream();
	}

	public static boolean contains(Identifier id) {
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

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, ActionManager::new);
		DependencyManager.ACTIONS.register(ID, dependencies -> dependencies.add(ConditionManager.ID));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ConditionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));

	}

}
