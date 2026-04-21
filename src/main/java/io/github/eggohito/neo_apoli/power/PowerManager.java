package io.github.eggohito.neo_apoli.power;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.api.event.PowerPreparation;
import io.github.eggohito.neo_apoli.api.event.PowerReloadEvents;
import io.github.eggohito.neo_apoli.api.event.ReloadableServerResourcesEvents;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizePowerTagsS2CPacket;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizePowersS2CPacket;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonObjectWithSource;
import io.github.eggohito.neo_apoli.resource.json.JsonReloadListener;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class PowerManager implements JsonReloadListener {

	public static final ResourceLocation ID = NeoApoli.id("manager/powers");
	public static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.POWERS.invoker()::add).build();

	public static final TagEntry.Lookup<PowerHolder<?>> TAG_LOOKUP = new TagEntry.Lookup<>() {

		@Nullable
		@Override
		public PowerHolder<?> element(ResourceLocation id, boolean required) {
			return getHolderAsResult(PowerIdentifier.of(id)).result().orElse(null);
		}

		@Nullable
		@Override
		public Collection<PowerHolder<?>> tag(ResourceLocation id) {
			return getHoldersFromTag(id).result().orElse(null);
		}

		@Override
		public String toString() {
			return "Power manager";
		}

	};

	private static final String TAG_DIRECTORY = Registries.tagsDirPath(NeoApoliRegistryKeys.POWER);
	private static final String DIRECTORY = Registries.elementsDirPath(NeoApoliRegistryKeys.POWER);

	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	private static final Logger LOGGER = LoggerFactory.getLogger(PowerManager.class);
	private static final TagLoader<PowerHolder<?>> TAG_LOADER = new TagLoader<>((id, required) -> getHolderAsResult(PowerIdentifier.of(id)).result(), TAG_DIRECTORY);

	private static final Object2ObjectOpenHashMap<PowerIdentifier, PowerHolder<?>> BY_ID = new Object2ObjectOpenHashMap<>();
	private static final Map<Power, PowerIdentifier> BY_POWER = new IdentityHashMap<>();

	private static final Object2ObjectOpenHashMap<ResourceLocation, List<TagLoader.EntryWithSource>> PREPARED_TAGS = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<ResourceLocation, List<PowerHolder<?>>> TAGS = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> ops;

	PowerManager(HolderLookup.Provider wrapperLookup) {
		this.ops = wrapperLookup.createSerializationContext(JsonOps.INSTANCE);
	}

	@Override
	public CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {

		CompletableFuture<Map<ResourceLocation, JsonObjectWithSource>> preparedElementsFuture = CompletableFuture
			.supplyAsync(() -> this.prepareElements(manager, Profiler.get()), prepareExecutor);
		CompletableFuture<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> preparedTagsFuture = CompletableFuture
			.supplyAsync(() -> this.preparePendingTags(manager, Profiler.get()), prepareExecutor);

		return preparedTagsFuture.thenCombine(preparedElementsFuture, Pair::of)
			.thenCompose(synchronizer::wait)
			.thenAcceptAsync(preparedTagsAndElements -> this.applyElements(preparedTagsAndElements.getSecond(), manager, Profiler.get()), applyExecutor);

	}

	private Map<ResourceLocation, List<TagLoader.EntryWithSource>> preparePendingTags(ResourceManager manager, ProfilerFiller ignoredProfiler) {

		PREPARED_TAGS.clear();
		Map<ResourceLocation, List<TagLoader.EntryWithSource>> pendingTags = TAG_LOADER.load(manager);

		PREPARED_TAGS.putAll(pendingTags);
		PREPARED_TAGS.trim();

		return PREPARED_TAGS;

	}

	private static void applyPendingTags(ReloadableServerResources ignored) {

		if (PREPARED_TAGS.isEmpty()) {
			return;
		}

		LOGGER.info("Parsing power tags from data packs...");
		TAGS.clear();

		TAGS.putAll(TAG_LOADER.build(PREPARED_TAGS));
		LOGGER.info("Finished parsing power tags from data packs. Parsed {} power tag(s)", TAGS.size());

		PREPARED_TAGS.clear();
		TAGS.trim();

	}

	private Map<ResourceLocation, JsonObjectWithSource> prepareElements(ResourceManager manager, ProfilerFiller ignoredProfiler) {

		Map<ResourceLocation, JsonObjectWithSource> prepared = new Object2ObjectOpenHashMap<>();
		manager.listResources(DIRECTORY, this::supportsFormat).forEach((fileId, resource) -> {

			String packId = resource.sourcePackId();
			ResourceLocation resourceId = this.trimExtension(fileId, DIRECTORY);

			try (BufferedReader resourceReader = resource.openAsReader()) {

				JsonFormat jsonFormat = this.getFormat(fileId);
				GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, jsonFormat));

				switch (GSON.fromJson(gsonReader, JsonElement.class)) {
					case JsonObject jsonObject when MiscUtil.isResourceConditionFulfilled(resourceId, jsonObject, DIRECTORY, ops) -> {

						jsonObject.addProperty(PowerHolder.ID_KEY, resourceId.toString());
						var newElement = new JsonObjectWithSource(packId, jsonObject, jsonFormat);

						if (prepared.putIfAbsent(resourceId, newElement) != null) {
							throw new IllegalStateException("Duplicate of a power JSON with the same name, but different file extension! (prev. file extension: " + prepared.get(resourceId).format().name().toLowerCase(Locale.ROOT) + ")");
						}

						else {
							PowerPreparation.EVENT.invoker().prepare(resourceId, newElement, DIRECTORY, ops);
						}

					}
					case JsonObject ignored -> {
						//	No-op since the resource conditions weren't fulfilled
					}
					case JsonElement jsonElement ->
						throw new JsonSyntaxException("Not a JSON object: " + jsonElement);
					case null ->
						throw new JsonSyntaxException("JSON file cannot be empty!");
					default -> {
						//  No-op since everything should already be handled by the 'jsonElement' case
					}
				}

			}

			catch (Exception e) {
				LOGGER.error("Error trying to prepare power JSON file \"{}\" from data pack [{}] (skipping): {}", fileId, packId, e);
			}

		});

		return prepared;

	}

	private void applyElements(Map<ResourceLocation, JsonObjectWithSource> prepared, ResourceManager manager, ProfilerFiller profiler) {

		PowerReloadEvents.BEFORE.invoker().beforeReload(manager, profiler);

		LOGGER.info("Parsing powers from data packs...");
		startLoading();

		prepared.forEach((id, elementWithSource) -> {

			ResourceLocationUtil.setCurrent(id);
			MiscUtil.handleResult(
				PowerHolder.CODEC.parse(ops, elementWithSource.element()),
				PowerManager::register,
				warning -> LOGGER.warn("Found warnings while parsing power {} from data pack [{}]: {}", id, elementWithSource.source(), warning),
				error -> LOGGER.error("Error trying to parse power {} from data pack [{}] (skipping): {}", id, elementWithSource.source(), error)
			);

			ResourceLocationUtil.setCurrent(null);

		});

		LOGGER.info("Finished parsing powers from data packs. Parsed {} power(s)", BY_ID.size());
		endLoading();

		PowerReloadEvents.AFTER.invoker().afterReload(manager, profiler);

	}

	@ApiStatus.Internal
	public static void init() {

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, PowerManager::new);
		DependencyManager.POWERS.register(ID, dependencies -> dependencies.add(ActionManager.ID));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ActionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));

		PowerPreparation.EVENT.register(MultiplePower.ID, MultiplePower::preProcessSubPowers);

		ReloadableServerResourcesEvents.AFTER_LOAD.addPhaseOrdering(ActionManager.ID, ID);
		ReloadableServerResourcesEvents.AFTER_LOAD.register(ID, resources -> {
			validate(resources);
			applyPendingTags(resources);
		});

	}

	private static void validate(ReloadableServerResources resources) {

		if (BY_ID.isEmpty()) {
			return;
		}

		ObjectIterator<PowerHolder<?>> iterator = BY_ID.values().iterator();
		int size = BY_ID.size();

		LOGGER.info("Validating {} power(s)...", size);

		while (iterator.hasNext()) {

			PowerHolder<?> powerHolder = iterator.next();
			Power power = powerHolder.value();

			Reporter reporter = new Reporter("{\"" + powerHolder.id() + "\"}");
			Context.Validator validator = new Context.Validator(power.getType().keySet(), reporter).withResolver(MiscUtil.getLookupProvider(resources));

			power.validate(validator);
			reporter.getErrorsFlattened().ifPresent(error -> {

				LOGGER.error("Found errors while validating {} {}", powerHolder.id().asDisplayString(false), error);

				BY_POWER.remove(power);
				iterator.remove();

			});

		}

		LOGGER.info("Finished validating {} power(s). Power manager contains {} power(s)", size, BY_ID.size());
		BY_ID.trim();

	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}

	@ApiStatus.Internal
	public static void sendSyncPayload(ServerPlayer player) {

		if (!player.server.isPublished()) {
			return;
		}

		Set<PowerHolder<?>> filtered = BY_ID.values()
			.stream()
			.filter(Predicate.not(PowerHolder::isSubPower))
			.collect(Collectors.toCollection(ObjectOpenHashSet::new));

		LOGGER.info("Sent {} power(s) to player {}!", filtered.size(), player.getName().getString());
		ServerPlayNetworking.send(player, new SynchronizePowersS2CPacket(filtered));

	}

	@ApiStatus.Internal
	public static void sendTagSyncPayload(ServerPlayer player) {

		if (!player.server.isPublished()) {
			return;
		}

		LOGGER.info("Sent {} power tag(s) to player {}!", TAGS.size(), player.getName().getString());
		ServerPlayNetworking.send(player, new SynchronizePowerTagsS2CPacket(TAGS));

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveSyncPayload(SynchronizePowersS2CPacket payload) {

		startLoading();
		payload.powers().forEach(PowerManager::register);
		endLoading();

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveSyncTagPayload(SynchronizePowerTagsS2CPacket payload) {

		TAGS.clear();
		TAGS.putAll(payload.powerTags());
		TAGS.trim();

	}

	public static DataResult<List<PowerHolder<?>>> getHoldersFromTag(TagKey<PowerHolder<?>> tag) {
		return getHoldersFromTag(tag.location());
	}

	public static DataResult<List<PowerHolder<?>>> getHoldersFromTag(ResourceLocation tagId) {
		return Optional.ofNullable(TAGS.get(tagId))
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error(() -> "Unknown power tag: " + tagId));
	}

	public static DataResult<PowerHolder<?>> getHolderAsResult(PowerIdentifier id) {
		return contains(id)
			? DataResult.success(BY_ID.get(id))
			: DataResult.error(() -> "Referenced " + id.asDisplayString(false) + " doesn't exist!");
	}

	public static PowerHolder<?> getHolder(PowerIdentifier id) {
		return getHolderAsResult(id).getOrThrow(IllegalArgumentException::new);
	}

	public static DataResult<Power> getAsResult(PowerIdentifier id) {
		return getHolderAsResult(id).map(PowerHolder::value);
	}

	public static Power get(PowerIdentifier id) {
		return getAsResult(id).getOrThrow(IllegalArgumentException::new);
	}

	public static DataResult<PowerIdentifier> getIdAsResult(Power power) {
		return containsId(power)
			? DataResult.success(BY_POWER.get(power))
			: DataResult.error(() -> power + " doesn't correspond to any IDs!");
	}

	public static PowerIdentifier getId(Power power) {
		return getIdAsResult(power).getOrThrow(IllegalArgumentException::new);
	}

	public static Set<ResourceLocation> tags() {
		return TAGS.keySet();
	}

	public static Set<PowerIdentifier> ids() {
		return new ObjectOpenHashSet<>(BY_ID.keySet());
	}

	public static Collection<PowerHolder<?>> holders() {
		return new ObjectOpenHashSet<>(BY_ID.values());
	}

	public static boolean contains(PowerIdentifier id) {
		return BY_ID.containsKey(id);
	}

	public static boolean containsId(Power power) {
		return BY_POWER.containsKey(power);
	}

	private static <P extends Power> void register(PowerHolder<P> powerHolder) {

		PowerIdentifier powerId = powerHolder.id();
		Power power = powerHolder.value();

		BY_ID.put(powerId, powerHolder);
		BY_POWER.put(power, powerId);

		if (power instanceof MultiplePower multiplePower) {

			if (powerId.isSubPower()) {
				throw new IllegalStateException("Tried to register \"" + powerId.asDisplayString(false) + " with \"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, PowerTypes.MULTIPLE) + "\" power type, which is not allowed!");
			}

			else {
				multiplePower.getSubPowers().forEach(PowerManager::register);
			}

		}

	}

	private static void startLoading() {
		BY_ID.clear();
		BY_POWER.clear();
	}

	private static void endLoading() {
		BY_ID.trim();
	}

}
