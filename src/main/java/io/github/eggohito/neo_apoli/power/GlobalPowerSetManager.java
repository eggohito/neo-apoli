package io.github.eggohito.neo_apoli.power;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.api.event.ReloadableServerResourcesEvents;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonObjectWithSource;
import io.github.eggohito.neo_apoli.resource.json.JsonReloadListener;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import io.github.eggohito.neo_apoli.util.tag.LazyTagLike;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalPowerSetManager extends SimplePreparableReloadListener<Map<ResourceLocation, List<JsonObjectWithSource>>> implements JsonReloadListener {

	public static final ResourceLocation ID = NeoApoli.id("manager/global_power_sets");
	public static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.GLOBAL_POWER_SETS.invoker()::add).build();

	private static final String DIRECTORY = Registries.elementsDirPath(NeoApoliRegistryKeys.GLOBAL_POWER_SET);
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalPowerSetManager.class);

	private static final Object2ObjectOpenHashMap<ResourceLocation, GlobalPowerSet> BY_ID = new Object2ObjectOpenHashMap<>();

	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	private final RegistryOps<JsonElement> ops;

	public GlobalPowerSetManager(HolderLookup.Provider lookupProvider) {
		this.ops = lookupProvider.createSerializationContext(JsonOps.INSTANCE);
	}

	@Override
	protected Map<ResourceLocation, List<JsonObjectWithSource>> prepare(ResourceManager manager, ProfilerFiller profiler) {

		Map<ResourceLocation, List<JsonObjectWithSource>> prepared = new Object2ObjectOpenHashMap<>();
		manager.listResourceStacks(DIRECTORY, this::supportsFormat).forEach((fileId, resources) -> {

			ResourceLocation resourceId = this.trimExtension(fileId, DIRECTORY);
			JsonFormat jsonFormat = this.getFormat(fileId);

			resources.forEach(resource -> {

				String packName = resource.sourcePackId();
				try (BufferedReader resourceReader = resource.openAsReader()) {

					GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, jsonFormat));
					JsonElement jsonElement = GSON.fromJson(gsonReader, JsonElement.class);

					switch (jsonElement) {
						case JsonObject jsonObject when MiscUtil.isResourceConditionFulfilled(resourceId, jsonObject, DIRECTORY, ops) -> {
							var elementWithSource = new JsonObjectWithSource(packName, jsonObject, jsonFormat);
							prepared
								.computeIfAbsent(resourceId, k -> new ObjectArrayList<>())
								.add(elementWithSource);
						}
						case JsonObject ignored -> {
							//	No-op
						}
						case null ->
							throw new JsonSyntaxException("JSON file cannot be empty!");
						default ->
							throw new JsonSyntaxException("Not a JSON object: " + jsonElement);
					}

				}

				catch (Exception e) {
					LOGGER.error("Error trying to prepare global power set JSON file \"{}\" from data pack [{}] (skipping): {}", fileId, packName, e);
				}

			});

		});

		return prepared;

	}

	@Override
	protected void apply(Map<ResourceLocation, List<JsonObjectWithSource>> prepared, ResourceManager manager, ProfilerFiller profiler) {

		LOGGER.info("Parsing global power sets from data packs...");
		BY_ID.clear();

		Map<ResourceLocation, List<GlobalPowerSet.WithSource>> parsed = new Object2ObjectOpenHashMap<>();
		prepared.forEach((id, elementWithSources) -> {

			ResourceLocationUtil.setCurrent(id);
			elementWithSources.forEach(jsonObjectWithSource -> GlobalPowerSet.CODEC.compressedDecode(ops, jsonObjectWithSource.element())
				.ifError(error -> LOGGER.error("Error trying to parse global power set \"{}\" from data pack [{}] (skipping): {}", id, jsonObjectWithSource.source(), error.message()))
				.ifSuccess(set -> parsed
					.computeIfAbsent(id, k -> new ObjectArrayList<>())
					.add(new GlobalPowerSet.WithSource(set, id, jsonObjectWithSource.source()))));

			ResourceLocationUtil.setCurrent(null);

		});

		LOGGER.info("Finished parsing global power sets. Merging similar global power sets...");

		parsed.forEach((id, sets) -> sets
			.stream()
			.reduce(GlobalPowerSetManager::merge)
			.ifPresent(withSource -> BY_ID.put(id, withSource.set())));

		LOGGER.info("Finished merging global power sets. Merged {} global power set(s)", parsed.values().stream().mapToInt(Collection::size).sum());

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
	public static void init() {

	}

	private static GlobalPowerSet.WithSource merge(GlobalPowerSet.WithSource first, GlobalPowerSet.WithSource second) {

		LazyTagLike.Builder<EntityType<?>> entityTypes = new LazyTagLike.Builder<>(BuiltInRegistries.ENTITY_TYPE);
		LazyTagLike.Builder<PowerEntry<?>> powers = new LazyTagLike.Builder<>(PowerManager.TAG_LOOKUP);

		boolean replace = second.set().replace();
		int order = first.set().order();

		if (replace) {

			LOGGER.warn("Global power set \"{}\" from data pack [{}] has been replaced with a similar one from data pack [{}]!", second.id(), first.source(), second.source());

			order = second.set().order();

		}

		else {
			entityTypes.addAll(first.set().entityTypes().entries());
			powers.addAll(first.set().powers().entries());
		}

		entityTypes.addAll(second.set().entityTypes().entries());
		powers.addAll(second.set().powers().entries());

		GlobalPowerSet set = new GlobalPowerSet(
			entityTypes.build(),
			powers.build(),
			replace,
			order
		);

		return new GlobalPowerSet.WithSource(set, second.id(), second.source());

	}

	private static void validate(ReloadableServerResources resources) {

		if (BY_ID.isEmpty()) {
			return;
		}

		var iterator = BY_ID.object2ObjectEntrySet().fastIterator();
		int size = BY_ID.size();

		LOGGER.info("Validating {} global power set(s)...", size);

		while (iterator.hasNext()) {

			var entry = iterator.next();

			ResourceLocation id = entry.getKey();
			GlobalPowerSet set = entry.getValue();

			Reporter reporter = new Reporter("{\"" + id + "\"");
			Context.Validator validator = new Context.Validator(LootContextParamSets.EMPTY, reporter).withResolver(MiscUtil.getLookupProvider(resources));

			set.validate(validator);

			reporter.getErrorsFlattened().ifPresent(error -> {
				LOGGER.warn("Found error(s) while validating global power set \"{}\" {}", id, error);
				iterator.remove();
			});

		}

		LOGGER.info("Finished validating {} global power set(s). Global power set manager contains {} global power set(s)", size, BY_ID.size());
		BY_ID.trim();

	}

	public static Set<ResourceLocation> ids() {
		return new ObjectOpenHashSet<>(BY_ID.keySet());
	}

	public static List<GlobalPowerSet> sets() {
		return new ObjectArrayList<>(BY_ID.values());
	}

	public static List<GlobalPowerSet> getApplicableSets(Entity entity) {

		List<GlobalPowerSet> applicableSets = new ObjectArrayList<>();

		for (var set : sets()) {

			if (set.doesApply(entity)) {
				applicableSets.add(set);
			}

		}

		applicableSets.sort(GlobalPowerSet::compareTo);
		return applicableSets;

	}

	public static Set<PowerEntry<?>> flattenPowers(Collection<GlobalPowerSet> sets) {

		Set<PowerEntry<?>> entries = new ObjectOpenHashSet<>();
		for (var set : sets) {
			entries.addAll(set.powers().elements());
		}

		return entries;

	}

	private static void applyAll(Entity entity, ServerLevel serverLevel) {

		PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(entity);

		List<GlobalPowerSet> applicableSets = getApplicableSets(entity);
		Set<PowerEntry<?>> expectedEntries = flattenPowers(applicableSets);

		//	Revoke all powers that are from the global power source, but not within the expected
		//	set of powers collected from all global power sets
		for (var entryFromSource : powersComponent.getAllFromSource(GlobalPowerSet.SOURCE)) {

			if (!expectedEntries.contains(entryFromSource)) {
				powersComponent.revokePower(entryFromSource, GlobalPowerSet.SOURCE);
			}

		}

		//	Re-add all the expected powers collected from all global power sets
		for (var expectedEntry : expectedEntries) {
			powersComponent.grantPower(expectedEntry, GlobalPowerSet.SOURCE);
		}

		powersComponent.checkForUpdates();

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, GlobalPowerSetManager::new);
		DependencyManager.GLOBAL_POWER_SETS.register(ID, dependencies -> dependencies.add(PowerManager.ID));

		ReloadableServerResourcesEvents.RegistryTagUpdate.AFTER.addPhaseOrdering(PowerManager.ID, ID);
		ReloadableServerResourcesEvents.RegistryTagUpdate.AFTER.register(ID, GlobalPowerSetManager::validate);

		ServerEntityEvents.ENTITY_LOAD.addPhaseOrdering(PowerManager.ID, ID);
		ServerEntityEvents.ENTITY_LOAD.register(ID, GlobalPowerSetManager::applyAll);

	}

}
