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
import io.github.eggohito.neo_apoli.resource.JsonResourceReloader;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class ConditionManager extends SinglePreparationResourceReloader<Map<ConditionCategory<?>, Map<Identifier, JsonResourceReloader.Entry>>> implements JsonResourceReloader {

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

	@Override
	protected Map<ConditionCategory<?>, Map<Identifier, Entry>> prepare(ResourceManager manager, Profiler profiler) {

		Map<ConditionCategory<?>, Map<Identifier, Entry>> prepared = new Object2ObjectOpenHashMap<>();
		for (var category : NeoApoliRegistries.CONDITION_CATEGORY) {

			String directory = RegistryKeys.getPath(category.registryRef());
			manager.findResources(directory, this::supportsJsonFormat).forEach((fileId, resource) -> {

				String packName = resource.getPackId();
				Identifier resourceId = this.trimExtension(fileId, directory);

				try (BufferedReader resourceReader = resource.getReader()) {

					GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, this.getJsonFormat(fileId)));
					JsonElement jsonElement = GSON.fromJson(gsonReader, JsonElement.class);

					if (jsonElement != null) {
						prepared.computeIfAbsent(category, k -> new Object2ObjectOpenHashMap<>()).put(resourceId, new Entry(packName, jsonElement));
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

	@Override
	protected void apply(Map<ConditionCategory<?>, Map<Identifier, Entry>> prepared, ResourceManager manager, Profiler profiler) {

		LOGGER.info("Parsing conditions from data packs...");
		startLoading();

		prepared.forEach((category, entries) -> entries.forEach((id, entry) -> category.baseCodec().parse(ops, entry.element())
			.ifSuccess(condition -> register(id, condition))
			.ifError(error -> LOGGER.info("Error trying to parse {} \"{}\" from data pack [{}] (skipping): {}", StringUtils.uncapitalize(category.toString()), id, entry.source(), error.message()))));

		StringBuilder message = new StringBuilder("Finished parsing conditions from data packs. Parsed " + BY_CATEGORY_AND_ID.size() + " condition(s) in total;");
		BY_CATEGORY_AND_ID.forEach((category, entries) -> message.append("\n\t - Parsed ").append(entries.size()).append(" ").append(StringUtils.uncapitalize(category.toString())).append("(s)"));

		LOGGER.info(message.toString());
		endLoading();

	}

	@ApiStatus.Internal
	public static void init() {

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, ConditionManager::new);
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

		Map<ConditionCategory<?>, Map<Identifier, Condition<?>>> filteredEntries = new Object2ObjectOpenHashMap<>();
		BY_CATEGORY_AND_ID.forEach((category, entries) -> entries.forEach((id, entry) -> filteredEntries
			.computeIfAbsent(category, k -> new Object2ObjectOpenHashMap<>())
			.put(id, entry.value())));

		LOGGER.info("Sent {} condition(s) to player {}!", filteredEntries.size(), player.getName().getString());
		ServerPlayNetworking.send(player, new SynchronizeConditionsS2CPacket(filteredEntries));

	}

	@ApiStatus.Internal
	public static void receiveSyncPayload(SynchronizeConditionsS2CPacket payload) {
		startLoading();
		payload.conditions().forEach((category, entries) -> entries.forEach(ConditionManager::register));
		endLoading();
	}

	@SuppressWarnings("unchecked")
	public static <C extends Condition<?>> DataResult<ConditionEntry<C>> getEntryAsResult(ConditionCategory<C> category, Identifier id) {

		Map<Identifier, ConditionEntry<?>> entries = BY_CATEGORY_AND_ID.getOrDefault(category, new Object2ObjectOpenHashMap<>());
		ConditionEntry<?> entry = entries.get(id);

		if (entry != null) {
			return DataResult.success((ConditionEntry<C>) entry);
		}

		else {
			return DataResult.error(() -> category + " with ID \"" + id + "\" does not exist!");
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
			: DataResult.error(() -> condition + " doesn't correspond to any identifiers!");
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

	private static void register(Identifier id, Condition<?> condition) {
		BY_VALUES.put(condition, id);
		BY_CATEGORY_AND_ID
			.computeIfAbsent(condition.getCategory(), k -> new Object2ObjectOpenHashMap<>())
			.put(id, new ConditionEntry<>(id, condition));
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
