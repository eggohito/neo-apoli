package io.github.eggohito.neo_apoli.power;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.event.PowerPreparationCallback;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableRegistriesAccessor;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizePowerTagsS2CPacket;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizePowersS2CPacket;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.JsonResourceReloader;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerEntry;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.DataPackContents;
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

public final class PowerManager implements JsonResourceReloader {

	private static final Logger LOGGER = LoggerFactory.getLogger(PowerManager.class);
	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();
	
	public static final Identifier ID = NeoApoli.id("powers");
	public static final Set<Identifier> DEPENDENCIES = Util.make(new ObjectOpenHashSet<>(), set -> set.add(ActionManager.ID));

	private static final Object2ObjectOpenHashMap<Identifier, List<PowerEntry<?>>> TAGS = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<PowerReference, PowerEntry<?>> BY_REFERENCE = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<Power, PowerReference> BY_POWER = new Object2ObjectOpenHashMap<>();

	private final TagGroupLoader<PowerEntry<?>> tagLoader = new TagGroupLoader<>((id, required) -> getEntryAsResult(PowerReference.ofPower(id)).result(), RegistryKeys.getTagPath(NeoApoliRegistryKeys.POWER));
	private final RegistryOps<JsonElement> ops;

	public PowerManager(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.ops = wrapperLookup.getOps(JsonOps.INSTANCE);
	}

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {

		CompletableFuture<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> preparedTagsFuture = CompletableFuture
			.supplyAsync(() -> this.prepareTags(manager, Profilers.get()), prepareExecutor);
		CompletableFuture<Map<PowerReference.Power, Entry>> preparedElementsFuture = CompletableFuture
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

	private Map<Identifier, List<TagGroupLoader.TrackedEntry>> prepareTags(ResourceManager manager, Profiler profiler) {
		return tagLoader.loadTags(manager);
	}

	private void applyTags(Map<Identifier, List<TagGroupLoader.TrackedEntry>> prepared, ResourceManager manager, Profiler profiler) {

		LOGGER.info("Parsing power tags from data packs...");
		TAGS.clear();

		TAGS.putAll(this.tagLoader.buildGroup(prepared));

		LOGGER.info("Finished parsing power tags from data packs. Parsed {} power tag(s)", TAGS.size());
		TAGS.trim();

	}

	private Map<PowerReference.Power, Entry> prepareElements(ResourceManager manager, Profiler profiler) {

		Map<PowerReference.Power, Entry> prepared = new Object2ObjectOpenHashMap<>();
		String directory = RegistryKeys.getPath(NeoApoliRegistryKeys.POWER);

		manager.findResources(directory, this::supportsJsonFormat).forEach((fileId, resource) -> {

			String packName = resource.getPackId();
			Identifier resourceId = this.trimExtension(fileId, directory);

			try (BufferedReader resourceReader = resource.getReader()) {

				GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, this.getJsonFormat(fileId)));
				JsonElement jsonElement = GSON.fromJson(gsonReader, JsonElement.class);

				if (jsonElement == null) {
					throw new JsonSyntaxException("JSON file cannot be empty!");
				}

				else if (jsonElement instanceof JsonObject jsonObject) {

					if (MiscUtil.isResourceConditionFulfilled(resourceId, jsonObject, directory, ops)) {

						Entry entry = new Entry(packName, jsonObject);
						PowerPreparationCallback.EVENT.invoker().prepare(resourceId, entry, directory, ops);

						prepared.put(PowerReference.ofPower(resourceId), entry);

					}

				}

				else {
					throw new JsonSyntaxException("Not a JSON object: " + jsonElement);
				}

			}

			catch (Exception e) {
				LOGGER.error("Error trying to prepare power JSON file \"{}\" from data pack [{}] (skipping): {}", fileId, packName, e);
			}

		});

		return prepared;

	}

	private void applyElements(Map<PowerReference.Power, Entry> prepared, ResourceManager manager, Profiler profiler) {

		LOGGER.info("Parsing powers from data packs...");
		startLoading();

		prepared.forEach((powerReference, jsonEntry) -> {

			JsonObject powerEntryJson = new JsonObject();

			powerEntryJson.addProperty(PowerEntry.REFERENCE_KEY, powerReference.toString());
			powerEntryJson.add(PowerEntry.VALUE_KEY, jsonEntry.element());

			PowerEntry.CODEC.parse(ops, powerEntryJson)
				.ifSuccess(PowerManager::register)
				.ifError(error -> LOGGER.error("Error trying to parse {} from data pack [{}] (skipping): {}", powerReference, jsonEntry.source(), error.message()));

		});

		LOGGER.info("Finished parsing powers from data packs. Parsed {} power(s)", BY_REFERENCE.size());
		endLoading();

	}

	@ApiStatus.Internal
	public static void init() {

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, PowerManager::new);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ActionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));

		PowerPreparationCallback.EVENT.register(MultiplePower.ID, MultiplePower::preProcessSubPowers);

	}

	@ApiStatus.Internal
	public static void validate(DataPackContents dataPackContents) {

		if (BY_REFERENCE.isEmpty()) {
			return;
		}

		ObjectIterator<PowerEntry<?>> entryIterator = BY_REFERENCE.values().iterator();
		int prevSize = BY_REFERENCE.size();

		NeoApoli.LOGGER.info("Validating {} power(s)...", prevSize);

		while (entryIterator.hasNext()) {

			PowerEntry<?> entry = entryIterator.next();
			Power power = entry.value();

			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter()
				.withContextType(power.getSerializer().contextType())
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) dataPackContents.getReloadableRegistries()).getRegistries());

			power.validate(reporter);

			if (!reporter.hasAnyErrors()) {
				continue;
			}

			NeoApoli.LOGGER.warn("Error validating {} due to error(s) {}", entry.reference().asDisplayString(false), reporter.getErrorsAsString());

			BY_POWER.remove(power);
			entryIterator.remove();

		}

		NeoApoli.LOGGER.info("Finished validating {} power(s). Power manager contains {} power(s)", prevSize, BY_REFERENCE.size());

		BY_POWER.trim();
		BY_REFERENCE.trim();

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

		Map<PowerReference.Power, Power> filteredEntries = new Object2ObjectOpenHashMap<>();
		BY_REFERENCE.forEach((reference, powerEntry) -> {

			if (reference instanceof PowerReference.Power powerReference) {
				filteredEntries.put(powerReference, powerEntry.value());
			}

		});

		LOGGER.info("Sent {} power(s) to player {}!", filteredEntries.size(), player.getName().getString());
		ServerPlayNetworking.send(player, new SynchronizePowersS2CPacket(filteredEntries));

	}

	@ApiStatus.Internal
	public static void sendTagSyncPayload(ServerPlayerEntity player) {

		if (!player.server.isRemote()) {
			return;
		}

		LOGGER.info("Sent {} power tag(s) to player {}!", TAGS.size(), player.getName().getString());
		ServerPlayNetworking.send(player, new SynchronizePowerTagsS2CPacket(TAGS));

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveSyncPayload(SynchronizePowersS2CPacket payload, ClientPlayNetworking.Context context) {

		Objects.requireNonNull(context.client(), "client");
		Objects.requireNonNull(context.responseSender(), "responseSender");

		startLoading();
		payload.powers().forEach(PowerManager::register);
		endLoading();

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveSyncTagPayload(SynchronizePowerTagsS2CPacket payload, ClientPlayNetworking.Context context) {

		Objects.requireNonNull(context.client(), "client");
		Objects.requireNonNull(context.responseSender(), "responseSender");

		TAGS.clear();
		TAGS.putAll(payload.powerTags());
		TAGS.trim();

	}

	public static List<PowerEntry<?>> getEntriesFromTagOrEmpty(TagKey<Power> tag) {
		return TAGS.getOrDefault(tag.id(), new ObjectArrayList<>());
	}

	public static List<PowerEntry<?>> getEntriesFromTagOrEmpty(Identifier tagId) {
		return getEntriesFromTagOrEmpty(TagKey.of(NeoApoliRegistryKeys.POWER, tagId));
	}

	public static DataResult<PowerEntry<?>> getEntryAsResult(PowerReference reference) {
		return contains(reference)
			? DataResult.success(BY_REFERENCE.get(reference))
			: DataResult.error(() -> "Referenced \"" + reference.asDisplayString(false) + "\" doesn't exist!");
	}

	public static PowerEntry<?> getEntry(PowerReference reference) {
		return getEntryAsResult(reference).getOrThrow(IllegalArgumentException::new);
	}

	public static DataResult<Power> getAsResult(PowerReference reference) {
		return getEntryAsResult(reference).map(PowerEntry::value);
	}

	public static Power get(PowerReference reference) {
		return getAsResult(reference).getOrThrow(IllegalArgumentException::new);
	}

	public static DataResult<PowerReference> getReferenceAsResult(Power power) {
		return containsReference(power)
			? DataResult.success(BY_POWER.get(power))
			: DataResult.error(() -> "Power " + power + " doesn't correspond to any references!");
	}

	public static PowerReference getReference(Power power) {
		return getReferenceAsResult(power).getOrThrow(IllegalArgumentException::new);
	}

	public static Stream<PowerReference> streamReferences() {
		return BY_REFERENCE.keySet().stream();
	}

	public static boolean contains(PowerReference reference) {
		return BY_REFERENCE.containsKey(reference);
	}

	public static boolean containsReference(Power power) {
		return BY_POWER.containsKey(power);
	}

	private static <P extends Power> void register(PowerReference reference, P power) {
		register(new PowerEntry<>(reference, power));
	}

	private static <P extends Power> void register(PowerEntry<P> entry) {

		PowerReference reference = entry.reference();
		Power power = entry.value();

		power.getProperties().withReference(reference);

		BY_REFERENCE.put(reference, entry);
		BY_POWER.put(power, reference);

		if (power instanceof MultiplePower multiplePower) {

			switch (reference) {
				case PowerReference.Power ignored ->
					multiplePower.getSubPowers().forEach((subReference, subPower) -> register(new PowerEntry<>(subReference, subPower)));
				case PowerReference.SubPower subPowerReference ->
					throw new IllegalStateException("Tried to register " + subPowerReference.asDisplayString(false) + " with \"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, power.getSerializer()) + "\" power type, which isn't allowed!");
			}

		}

	}

	private static void startLoading() {
		BY_REFERENCE.clear();
		BY_POWER.clear();
	}

	private static void endLoading() {
		BY_REFERENCE.trim();
		BY_POWER.trim();
	}

	public record Entry(String source, JsonObject element) implements JsonResourceReloader.Entry {

	}

}
