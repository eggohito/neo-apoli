package io.github.eggohito.neo_apoli.power;

import com.google.gson.*;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.event.PowerLoadingEvents;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableRegistriesAccessor;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizePowersS2CPacket;
import io.github.eggohito.neo_apoli.power.internal.MultiplePower;
import io.github.eggohito.neo_apoli.resource.MultiDirectoryResourceReloader;
import io.github.eggohito.neo_apoli.util.PowerEntry;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.parsers.json.JsonFormat;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PowerManager extends MultiDirectoryResourceReloader {

	private static final Set<String> DIRECTORY_PREFIXES = new ObjectOpenHashSet<>();

	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();
	
	public static final Identifier ID = NeoApoli.id("powers");
	public static final Set<Identifier> DEPENDENCIES = Util.make(new ObjectOpenHashSet<>(), set -> set.add(ActionManager.ID));

	private static final Object2ObjectOpenHashMap<PowerReference, PowerEntry<?>> POWERS_BY_REFERENCE = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<Power, PowerReference> REFERENCES_BY_POWER = new Object2ObjectOpenHashMap<>();

	public PowerManager(RegistryWrapper.WrapperLookup wrapperLookup) {
		super(GSON, ResourceType.SERVER_DATA, wrapperLookup);
	}

	@ApiStatus.Internal
	public static void init() {

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ID, PowerManager::new);
		addDirectoryPrefix(NeoApoli.MOD_NAMESPACE);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ActionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));

		PowerLoadingEvents.BEFORE.register(MultiplePower.ID, MultiplePower::preProcessSubPowers);

	}

	@ApiStatus.Internal
	public static void validate(DataPackContents dataPackContents) {

		if (POWERS_BY_REFERENCE.isEmpty()) {
			return;
		}

		ObjectIterator<PowerEntry<?>> entryIterator = POWERS_BY_REFERENCE.values().iterator();
		int prevSize = POWERS_BY_REFERENCE.size();

		NeoApoli.LOGGER.info("Validating {} power(s)...", prevSize);

		while (entryIterator.hasNext()) {

			PowerEntry<?> entry = entryIterator.next();
			Power power = entry.value();

			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter()
				.withContextType(power.getContextType())
				.withWrapperLookup(((ReloadableRegistriesAccessor.LookupAccessor) dataPackContents.getReloadableRegistries()).getRegistries());

			power.validate(reporter);

			if (!reporter.hasAnyErrors()) {
				continue;
			}

			NeoApoli.LOGGER.warn("Error validating {} due to error(s) {}", entry.reference().asDisplayString(false), reporter.getErrorsAsString());

			REFERENCES_BY_POWER.remove(power);
			entryIterator.remove();

		}

		NeoApoli.LOGGER.info("Finished validating {} power(s). Registry contains {} power(s)", prevSize, POWERS_BY_REFERENCE.size());

		REFERENCES_BY_POWER.trim();
		POWERS_BY_REFERENCE.trim();

	}

	@Override
	protected void prepareSingle(Map<Identifier, Entry> prepared, String directory, Identifier resourceId, Entry entry) {

		if (entry.element() instanceof JsonObject jsonObject) {

			if (isResourceConditionFulfilled(resourceId, jsonObject, directory, ops)) {
				PowerLoadingEvents.BEFORE.invoker().beforeLoad(resourceId, entry, directory, ops);
				prepared.put(resourceId, entry);
			}

		}

		else {
			throw new JsonParseException("Not a JSON object: " + entry.element());
		}

	}

	@Override
	protected void apply(Map<Identifier, Entry> prepared, ResourceManager manager, Profiler profiler) {

		String simpleClassName = this.getClass().getSimpleName();
		profiler.push("[" + simpleClassName + "] start parsing powers");

		NeoApoli.LOGGER.info("Parsing powers from data packs...");
		startLoading();

		prepared.forEach((id, dataEntry) -> {

			JsonObject jsonObject = new JsonObject();
			PowerReference powerReference = PowerReference.ofPower(id);

			profiler.push("[" + simpleClassName + "] parsing power \"" + id + "\" from data pack {" + dataEntry.source() + "}");

			jsonObject.addProperty(PowerEntry.REFERENCE_KEY, powerReference.toString());
			jsonObject.add(PowerEntry.VALUE_KEY, dataEntry.element());

			try {

				PowerEntry<?> powerEntry = PowerEntry.CODEC.parse(ops, jsonObject).getOrThrow();
				Power power = powerEntry.value();

				power.getProperties().withReference(powerReference);

				if (power instanceof MultiplePower multiplePower) {
					multiplePower.getSubPowers().forEach((name, subPower) -> {

						PowerReference subPowerReference = PowerReference.ofSubPower(id, name);
						JsonObject subPowerJson = jsonObject.getAsJsonObject(PowerEntry.VALUE_KEY).getAsJsonObject(name);

						Entry subEntryData = new Entry(dataEntry.source(), subPowerJson);
						registerWithCallback(new PowerEntry<>(subPowerReference, subPower), subEntryData, ops);

					});
				}

				registerWithCallback(powerEntry, dataEntry, ops);

			}

			catch (Exception e) {
				NeoApoli.LOGGER.error("Error trying to parse {} from data pack [{}] (skipping): {}", powerReference, dataEntry.source(), e);
			}

			profiler.pop();

		});

		profiler.pop();

		NeoApoli.LOGGER.info("Finished parsing powers from data packs. Parsed {} power(s).", POWERS_BY_REFERENCE.size());
		endLoading();

	}

	@Override
	public Map<String, JsonFormat> getSupportedJsonFormats() {
		return NeoApoli.JSON_FORMATS;
	}

	@Override
	public Set<String> getDirectories() {

		String directory = "power";
		Set<String> directories = new ObjectOpenHashSet<>();

		for (String prefix : DIRECTORY_PREFIXES) {
			directories.add(prefix + "/" + directory);
		}

		directories.add(directory);
		return directories;

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
		return containsReference(power)
			? DataResult.success(REFERENCES_BY_POWER.get(power))
			: DataResult.error(() -> "Power " + power + " doesn't correspond to any references!");
	}

	public static PowerReference getReference(Power power) {
		return getReferenceAsResult(power).getOrThrow(IllegalArgumentException::new);
	}

	public static Stream<PowerReference> streamReferences() {
		return POWERS_BY_REFERENCE.keySet().stream();
	}

	public static boolean contains(PowerReference reference) {
		return POWERS_BY_REFERENCE.containsKey(reference);
	}

	public static boolean containsReference(Power power) {
		return REFERENCES_BY_POWER.containsKey(power);
	}

	public static void addDirectoryPrefix(String prefix) {
		DIRECTORY_PREFIXES.add(prefix);
	}

	private static void registerWithCallback(PowerEntry<?> powerEntry, Entry dataEntry, RegistryOps<JsonElement> registryOps) {
		register(powerEntry);
		PowerLoadingEvents.AFTER.invoker().afterLoad(powerEntry, dataEntry, registryOps);
	}

	private static void register(PowerEntry<?> entry) {

		PowerReference reference = entry.reference();

		POWERS_BY_REFERENCE.put(reference, entry);
		REFERENCES_BY_POWER.put(entry.value(), reference);

	}

	private static void startLoading() {
		POWERS_BY_REFERENCE.clear();
		REFERENCES_BY_POWER.clear();
	}

	private static void endLoading() {
		POWERS_BY_REFERENCE.trim();
		REFERENCES_BY_POWER.trim();
	}

}
