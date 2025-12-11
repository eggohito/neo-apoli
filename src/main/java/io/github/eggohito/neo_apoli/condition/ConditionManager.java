package io.github.eggohito.neo_apoli.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.ValueSuppliedElementCodec;
import io.github.eggohito.neo_apoli.event.DependencyManager;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizeConditionsS2CPacket;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.JsonReloadListener;
import io.github.eggohito.neo_apoli.util.DynamicResourceLocation;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class ConditionManager extends SimplePreparableReloadListener<Map<ResourceLocation, JsonReloadListener.Entry>> implements JsonReloadListener {

	private static final String DIRECTORY = Registries.elementsDirPath(NeoApoliRegistryKeys.CONDITION);
	private static final Logger LOGGER = LoggerFactory.getLogger(ConditionManager.class);

	private static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	public static final ResourceLocation ID = NeoApoli.id("manager/conditions");
	public static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.CONDITIONS.invoker()::add).build();

	private static final Object2ObjectOpenHashMap<ResourceLocation, Condition> BY_ID = new Object2ObjectOpenHashMap<>();
	private static final IdentityHashMap<Condition, ResourceLocation> BY_CONDITION = new IdentityHashMap<>();

	private final RegistryOps<JsonElement> ops;

	ConditionManager(HolderLookup.Provider wrapperLookup) {
		this.ops = wrapperLookup.createSerializationContext(JsonOps.INSTANCE);
	}

	@Override
	protected Map<ResourceLocation, Entry> prepare(ResourceManager manager, ProfilerFiller profiler) {

		Map<ResourceLocation, Entry> prepared = new Object2ObjectOpenHashMap<>();
		manager.listResources(DIRECTORY, this::supportsFormat).forEach((fileId, resource) -> {

			String packId = resource.sourcePackId();
			ResourceLocation resourceId = this.trimExtension(fileId, DIRECTORY);

			try (BufferedReader resourceReader = resource.openAsReader()) {

				GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, this.getFormat(fileId)));
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
				LOGGER.error("Error trying to prepare condition JSON file \"{}\" from data pack [{}] (skipping): {}", fileId, packId, e);
			}

		});

		return prepared;

	}

	@Override
	protected void apply(Map<ResourceLocation, Entry> prepared, ResourceManager manager, ProfilerFiller profiler) {

		LOGGER.info("Parsing conditions from data packs...");
		BY_ID.clear();

		prepared.forEach((id, entry) -> {
			DynamicResourceLocation.setCurrent(id);
			Condition.CODEC.parse(ops, entry.element())
				.ifSuccess(condition -> register(id, condition))
				.ifError(error -> LOGGER.error("Error trying to parse condition \"{}\" from data pack [{}] (skipping): {}", id, entry.source(), error.message()));
		});

		LOGGER.info("Finished parsing conditions from data packs. Parsed {} condition(s)", BY_ID.size());
		DynamicResourceLocation.setCurrent(null);

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
	public static void sendSyncPayload(ServerPlayer receiver) {

		if (!receiver.server.isPublished()) {
			return;
		}

		LOGGER.info("Sent {} condition(s) to player {}!", BY_ID.size(), receiver.getName().getString());
		ServerPlayNetworking.send(receiver, new SynchronizeConditionsS2CPacket(BY_ID));

	}

	@Environment(EnvType.CLIENT)
	@ApiStatus.Internal
	public static void receiveSyncPayload(SynchronizeConditionsS2CPacket payload, ClientPlayNetworking.Context context) {

		Objects.requireNonNull(context.client(), "client");
		Objects.requireNonNull(context.responseSender(), "responseSender");

		BY_ID.clear();
		BY_CONDITION.clear();

		BY_ID.putAll(payload.conditions());
		payload.conditions().forEach(ConditionManager::register);

		BY_ID.trim();

	}

	private static void register(ResourceLocation id, Condition condition) {
		BY_ID.put(id, condition);
		BY_CONDITION.put(condition, id);
	}

	public static DataResult<Condition> getAsResult(ResourceLocation id) {
		return contains(id)
			? DataResult.success(BY_ID.get(id))
			: DataResult.error(() -> "Condition with ID \"" + id + "\" does not exist!");
	}

	public static Condition get(ResourceLocation id) {
		return getAsResult(id).getOrThrow();
	}

	public static DataResult<ResourceLocation> getIdAsResult(Condition condition) {
		return containsId(condition)
			? DataResult.success(BY_CONDITION.get(condition))
			: DataResult.error(() -> condition + " doesn't correspond to any identifiers!");
	}

	public static ResourceLocation getId(Condition condition) {
		return getIdAsResult(condition).getOrThrow();
	}

	public static Stream<Condition> conditions() {
		return BY_ID.values().stream();
	}

	public static Stream<ResourceLocation> ids() {
		return BY_ID.keySet().stream();
	}

	public static boolean contains(ResourceLocation id) {
		return BY_ID.containsKey(id);
	}

	public static boolean containsId(Condition condition) {
		return BY_CONDITION.containsKey(condition);
	}

	public static ValueSuppliedElementCodec<Condition> createEntryCodec(boolean allowInlineDefinitions) {
		return new ValueSuppliedElementCodec<>(Condition.CODEC, allowInlineDefinitions, ConditionManager::getAsResult, ConditionManager::getIdAsResult);
	}

	public static void init() {

	}

	static {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, ConditionManager::new);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sendSyncPayload(player));
	}

}
