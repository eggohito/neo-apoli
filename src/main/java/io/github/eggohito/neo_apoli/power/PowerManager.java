package io.github.eggohito.neo_apoli.power;

import com.google.common.collect.HashBiMap;
import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.event.PowerLoadingEvents;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizePowersS2CPacket;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.PowerIdentifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
import java.util.Objects;
import java.util.Set;

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

	private static final Object2ObjectOpenHashMap<PowerIdentifier, Power> ID_TO_POWER = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<Power, PowerIdentifier> POWER_TO_ID = new Object2ObjectOpenHashMap<>();

	private static boolean init;

	private final RegistryWrapper.WrapperLookup wrapperLookup;
	private final RegistryOps<JsonElement> registryOps;

	PowerManager(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.wrapperLookup = wrapperLookup;
		this.registryOps = wrapperLookup.getOps(JsonOps.INSTANCE);
	}

	@ApiStatus.Internal
	public static void init() {

		if (init) {
			throw new RuntimeException("Power manager is already initialized!!");
		}

		else {

			ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, PowerManager::new);

			ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendPayload(player));
			PowerLoadingEvents.BEFORE.register(ID, (id, packData, directoryPath, registryOps) -> {

				if (packData.element() instanceof JsonObject jsonObject) {

					PowerType<?> powerType = Identifier.CODEC
						.parse(registryOps, jsonObject.get(Power.TYPE_KEY))
						.mapOrElse(NeoApoliRegistries.POWER_TYPE::get, error -> null);

					if (Objects.equals(powerType, PowerTypes.MULTIPLE)) {
						jsonObject.entrySet().removeIf(entry -> !MultiplePower.isKeyIgnored(entry.getKey()) && !isResourceConditionFulfilled(id, entry.getValue(), directoryPath, registryOps));
					}

				}

			});

			init = true;

		}

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

					else if (isResourceConditionFulfilled(resourceId, jsonElement, directoryPath, registryOps)) {

						PackData packData = new PackData(packName, jsonElement);

						PowerLoadingEvents.BEFORE.invoker().beforeLoad(resourceId, packData, directoryPath, registryOps);
						prepared.put(resourceId, packData);

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

		ID_TO_POWER.clear();
		NeoApoli.LOGGER.info("Parsing powers from data packs...");

		profiler.push("neo-apoli::parsePowers");
		prepared.forEach((id, packData) -> {

			profiler.push("neo-apoli::parsePowers::startParse->" + id + "::[" + packData.source() + "]");
			try {

				PowerIdentifier powerId = PowerIdentifier.of(id);
				Power power = Power.BASE_CODEC.decode(registryOps, packData.element())
					.getOrThrow()
					.getFirst();

				if (power instanceof MultiplePower multiplePower) {
					multiplePower.getSubPowers().forEach((name, subPower) -> {

						PowerIdentifier subPowerId = PowerIdentifier.subPower(id, name);
						JsonElement subPowerJson = Objects.requireNonNull(((JsonObject) packData.element()).get(name));

						PackData subPackData = new PackData(packData.source(), subPowerJson);
						register(subPowerId, subPower, subPackData, registryOps);

					});
				}

				register(powerId, power, packData, registryOps);

			}

			catch (Exception e) {
				String message = e.getMessage();
				NeoApoli.LOGGER.error("Error trying to parse power \"{}\" from data pack [{}] (skipping): {}", id, packData.source(), (message == null || message.isEmpty() ? e : message));
			}

			profiler.pop();

		});

		profiler.pop();

		NeoApoli.LOGGER.info("Finished parsing powers from data packs. Parsed {} power(s).", ID_TO_POWER.size());

		ID_TO_POWER.trim();
		POWER_TO_ID.trim();

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
	public static void sendPayload(ServerPlayerEntity player) {

		if (player.server.isRemote()) {
			NeoApoli.LOGGER.info("Sent {} power(s) to player {}!", ID_TO_POWER.size(), player.getName().getString());
			ServerPlayNetworking.send(player, new SynchronizePowersS2CPacket(HashBiMap.create(ID_TO_POWER)));
		}

	}

	@ApiStatus.Internal
	public static void receivePayload(SynchronizePowersS2CPacket payload) {

		ID_TO_POWER.clear();
		POWER_TO_ID.clear();

		ID_TO_POWER.putAll(payload.powers());
		POWER_TO_ID.putAll(payload.powers().inverse());

		ID_TO_POWER.trim();
		POWER_TO_ID.trim();

	}

	public static DataResult<Power> getAsResult(PowerIdentifier powerId) {
		return contains(powerId)
			? DataResult.success(ID_TO_POWER.get(powerId))
			: DataResult.error(() -> "No powers with power identifier \"" + powerId + "\" were found!");
	}

	public static PowerIdentifier getId(Power power) {

		if (POWER_TO_ID.containsKey(power)) {
			return POWER_TO_ID.get(power);
		}

		else {
			throw new IllegalArgumentException();
		}

	}

	public static Set<PowerIdentifier> getIds() {
		return ID_TO_POWER.keySet();
	}

	public static boolean contains(PowerIdentifier powerId) {
		return ID_TO_POWER.containsKey(powerId);
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

	@SuppressWarnings("UnstableApiUsage")
	private static boolean isResourceConditionFulfilled(Identifier id, JsonElement jsonElement, String directoryPath, RegistryOps<JsonElement> registryOps) {
		return !(jsonElement instanceof JsonObject jsonObject)
			|| ResourceConditionsImpl.applyResourceConditions(jsonObject, directoryPath, id, ((RegistryOpsAccessor) registryOps).getRegistryInfoGetter());
	}

	private static void register(PowerIdentifier id, Power power, PackData packData, RegistryOps<JsonElement> registryOps) {

		ID_TO_POWER.put(id, power);
		POWER_TO_ID.put(power, id);

		PowerLoadingEvents.AFTER.invoker().afterLoad(id, power, packData, registryOps);

	}

	public record PackData(String source, JsonElement element) {

	}

}
