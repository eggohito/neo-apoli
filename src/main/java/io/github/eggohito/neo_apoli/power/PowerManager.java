package io.github.eggohito.neo_apoli.power;

import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.event.PowerLoadingEvents;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizePowersS2CPacket;
import io.github.eggohito.neo_apoli.power.internal.MultiplePower;
import io.github.eggohito.neo_apoli.util.PowerEntry;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.fabricmc.fabric.mixin.resource.conditions.RegistryOpsAccessor;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PowerManager extends SinglePreparationResourceReloader<Map<Identifier, PowerManager.PackData>> implements IdentifiableResourceReloadListener {
	
	private static final Map<String, JsonFormat> JSON_FORMATS = Map.of(
		".json", JsonFormat.JSON,
		".json5", JsonFormat.JSON5,
		".jsonc", JsonFormat.JSONC
	);

	public static final Set<String> DIRECTORY_PATHS = Util.make(new ObjectOpenHashSet<>(), set -> {
		set.add("power");
		set.add("apoli/power");
	});

	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();
	
	private static final Identifier ID = NeoApoli.id("powers");
	private static final Set<Identifier> DEPENDENCIES = new ObjectOpenHashSet<>();

	private static final Object2ObjectOpenHashMap<PowerReference, PowerEntry<?>> POWERS_BY_REFERENCE = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<Power, PowerReference> REFERENCE_BY_POWER = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> registryOps;

	PowerManager(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.registryOps = wrapperLookup.getOps(JsonOps.INSTANCE);
	}

	@ApiStatus.Internal
	public static void init() {

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, PowerManager::new);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));
		PowerLoadingEvents.BEFORE.register(MultiplePower.ID, MultiplePower::preProcessSubPowers);

	}

	@Override
	protected Map<Identifier, PackData> prepare(ResourceManager manager, Profiler profiler) {

		Map<Identifier, PackData> prepared = new Object2ObjectOpenHashMap<>();
		profiler.push("neo-apoli::preparePowers");

		for (String directoryPath : DIRECTORY_PATHS) {

			profiler.push("neo-apoli::preparePowers::scan->" + directoryPath);
			manager.findResources(directoryPath, PowerManager::hasValidFormat).forEach((fileId, resource) -> {

				Identifier resourceId = trimExtension(fileId, directoryPath);
				String fileExtension = "." + FilenameUtils.getExtension(fileId.getPath());

				JsonFormat jsonFormat = JSON_FORMATS.get(fileExtension);
				String packName = resource.getPackId();

				if (prepared.containsKey(resourceId)) {
					NeoApoli.LOGGER.warn("Duplicate power JSON ignored with ID \"{}\"", resourceId);
					return;
				}

				profiler.push("neo-apoli::preparePowers::startPrep->" + fileId + "::[" + packName + "]");

				try (BufferedReader reader = resource.getReader()) {

					GsonReader gsonReader = new GsonReader(JsonReader.create(reader, jsonFormat));
					JsonElement jsonElement = GSON.fromJson(gsonReader, JsonElement.class);

					if (jsonElement == null) {
						throw new JsonParseException("JSON cannot be empty!");
					}

					else if (jsonElement instanceof JsonObject jsonObject) {

						if (isResourceConditionFulfilled(resourceId, jsonObject, directoryPath, registryOps)) {

							PackData packData = new PackData(packName, jsonObject);

							PowerLoadingEvents.BEFORE.invoker().beforeLoad(resourceId, packData, directoryPath, registryOps);
							prepared.put(resourceId, packData);

						}

					}

					else {
						throw new JsonSyntaxException("Not a JSON object: " + jsonElement);
					}

				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Error trying to prepare JSON of power \"{}\" from data pack [{}] (skipping): {}", resourceId, packName, e);
				}

				profiler.pop();

			});

			profiler.pop();

		}

		profiler.pop();
		return prepared;

	}

	@Override
	protected void apply(Map<Identifier, PackData> prepared, ResourceManager manager, Profiler profiler) {

		NeoApoli.LOGGER.info("Parsing powers from data packs...");
		startLoading();

		profiler.push("neo-apoli::parsePowers");
		prepared.forEach((id, packData) -> {

			PowerReference powerReference = PowerReference.ofPower(id);
			profiler.push("neo-apoli::parsePowers::startParse->" + id + "::[" + packData.source() + "]");

			try {

				JsonObject jsonObject = new JsonObject();
				packData.element().addProperty("id", powerReference.toString());

				jsonObject.addProperty(PowerEntry.REFERENCE_KEY, powerReference.toString());
				jsonObject.add(PowerEntry.VALUE_KEY, packData.element());

				PowerEntry<?> entry = PowerEntry.CODEC.parse(registryOps, jsonObject).getOrThrow();
				if (entry.value() instanceof MultiplePower multiplePower) {
					multiplePower.getSubPowers().forEach((name, subPower) -> {

						PowerReference subPowerReference = PowerReference.ofSubPower(id, name);
						JsonObject subPowerJson = jsonObject.getAsJsonObject(PowerEntry.VALUE_KEY).getAsJsonObject(name);

						PackData subPackData = new PackData(packData.source(), subPowerJson);
						registerWithCallback(new PowerEntry<>(subPowerReference, subPower), subPackData, registryOps);

					});
				}

				registerWithCallback(entry, packData, registryOps);

			}

			catch (Exception e) {
				String message = e.getMessage();
				NeoApoli.LOGGER.error("Error trying to parse {} from data pack [{}] (skipping): {}", powerReference, packData.source(), (message == null || message.isEmpty() ? e : message));
			}

			profiler.pop();

		});

		profiler.pop();

		NeoApoli.LOGGER.info("Finished parsing powers from data packs. Parsed {} power(s).", POWERS_BY_REFERENCE.size());
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

	@ApiStatus.Internal
	public static void sendSyncPayload(ServerPlayerEntity player) {

		if (!player.server.isRemote()) {
			return;
		}

		ObjectSet<PowerEntry<?>> entries = POWERS_BY_REFERENCE.values()
			.stream()
			.filter(Predicate.not(PowerEntry::isSubPower))
			.collect(Collectors.toCollection(ObjectOpenHashSet::new));

		NeoApoli.LOGGER.info("Sent {} power(s) to player {}!", POWERS_BY_REFERENCE.size(), player.getName().getString());
		ServerPlayNetworking.send(player, new SynchronizePowersS2CPacket(entries));

	}

	@ApiStatus.Internal
	public static void receiveSyncPayload(SynchronizePowersS2CPacket payload) {
		startLoading();
		payload.powers().forEach(PowerManager::register);
		endLoading();
	}

	public static DataResult<PowerEntry<?>> getEntryAsResult(PowerReference reference) {
		return contains(reference)
			? DataResult.success(POWERS_BY_REFERENCE.get(reference))
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

		if (containsReference(power)) {
			return DataResult.success(REFERENCE_BY_POWER.get(power));
		}

		else {
			return DataResult.error(() -> "Power " + power.toString() + " didn't have a power identifier as it wasn't registered!");
		}

	}

	public static PowerReference getReference(Power power) {
		return getReferenceAsResult(power).getOrThrow(IllegalArgumentException::new);
	}

	public static Stream<Power> streamPowers() {
		return REFERENCE_BY_POWER.keySet().stream();
	}

	public static Stream<PowerReference> streamIds() {
		return POWERS_BY_REFERENCE.keySet().stream();
	}

	public static boolean contains(PowerReference reference) {
		return POWERS_BY_REFERENCE.containsKey(reference);
	}

	public static boolean containsReference(Power power) {
		return REFERENCE_BY_POWER.containsKey(power);
	}

	private static Identifier trimExtension(Identifier fileId, String directoryPath) {
		String path = FilenameUtils.removeExtension(fileId.getPath()).substring(directoryPath.length() + 1);
		return Identifier.of(fileId.getNamespace(), path);
	}

	private static boolean hasValidFormat(Identifier fileId) {
		return JSON_FORMATS.keySet()
			.stream()
			.anyMatch(suffix -> fileId.getPath().endsWith(suffix));
	}

	public static boolean isResourceConditionFulfilled(Identifier id, JsonElement jsonElement, String directoryPath, RegistryOps<JsonElement> registryOps) {
		return !(jsonElement instanceof JsonObject jsonObject)
			|| isResourceConditionFulfilled(id, jsonObject, directoryPath, registryOps);
	}

	@SuppressWarnings("UnstableApiUsage")
	public static boolean isResourceConditionFulfilled(Identifier id, JsonObject jsonObject, String directoryPath, RegistryOps<JsonElement> registryOps) {
		return ResourceConditionsImpl.applyResourceConditions(jsonObject, directoryPath, id, ((RegistryOpsAccessor) registryOps).getRegistryInfoGetter());
	}

	private static void registerWithCallback(PowerEntry<?> entry, PackData packData, RegistryOps<JsonElement> registryOps) {
		register(entry);
		PowerLoadingEvents.AFTER.invoker().afterLoad(entry, packData, registryOps);
	}

	private static void register(PowerEntry<?> entry) {

		PowerReference reference = entry.reference();

		POWERS_BY_REFERENCE.put(reference, entry);
		REFERENCE_BY_POWER.put(entry.value(), reference);

	}

	private static void startLoading() {
		POWERS_BY_REFERENCE.clear();
		REFERENCE_BY_POWER.clear();
	}

	private static void endLoading() {
		POWERS_BY_REFERENCE.trim();
		REFERENCE_BY_POWER.trim();
	}

	public record PackData(String source, JsonObject element) {

	}

}
