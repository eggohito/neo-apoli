package io.github.eggohito.neo_apoli.power.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.api.event.PowerPreparation;
import io.github.eggohito.neo_apoli.api.event.PowerReloadEvents;
import io.github.eggohito.neo_apoli.api.event.ReloadableServerResourcesEvents;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public final class ServerPowerManager extends PowerManager implements IdentifiableResourceReloadListener {

	private static final TagLoader<PowerHolder<?>> TAG_LOADER = new TagLoader<>((id, required) -> getAsResult(PowerIdentifier.of(id)).result(), Registries.tagsDirPath(NeoApoliRegistryKeys.POWER));
	private static final JsonFileToIdConverter JSON_LOADER = JsonFileToIdConverter.registry(NeoApoliRegistryKeys.POWER);

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerPowerManager.class);
	private static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.POWERS.invoker()::add).build();

	private static volatile ImmutableMap<ResourceLocation, List<TagLoader.EntryWithSource>> pendingTags = ImmutableMap.of();
	private final DynamicOps<JsonElement> ops;

	ServerPowerManager(HolderLookup.Provider provider) {
		this.ops = provider.createSerializationContext(JsonOps.INSTANCE);
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}

	@Override
	public @NotNull CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {

		CompletableFuture<Map<ResourceLocation, JsonWithSource>> powersFuture = CompletableFuture
			.supplyAsync(() -> this.preparePowers(manager), backgroundExecutor);
		CompletableFuture<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> tagsFuture = CompletableFuture
			.supplyAsync(() -> TAG_LOADER.load(manager), backgroundExecutor);

		return powersFuture.thenCombine(tagsFuture, Pair::of)
			.thenCompose(barrier::wait)
			.thenAcceptAsync(pair -> this.applyAll(pair.getFirst(), pair.getSecond(), manager, Profiler.get()), gameExecutor);

	}

	private Map<ResourceLocation, JsonWithSource> preparePowers(ResourceManager manager) {

		Map<ResourceLocation, JsonWithSource> prepared = MiscUtil.collectJson(manager, JSON_LOADER, ops, LOGGER::error);
		prepared.forEach((id, jsonWithSource) -> PowerPreparation.EVENT.invoker().prepare(id, jsonWithSource, JSON_LOADER.directory(), ops));

		return prepared;

	}

	private void applyAll(Map<ResourceLocation, JsonWithSource> unparsedPowers, Map<ResourceLocation, List<TagLoader.EntryWithSource>> unparsedTags, ResourceManager manager, ProfilerFiller profiler) {

		ImmutableMap.Builder<PowerIdentifier, PowerHolder<?>> powersBuilder = ImmutableMap.builder();
		powers = ImmutableMap.of();

		PowerReloadEvents.BEFORE.invoker().beforeReload(manager, profiler);
		LOGGER.info("Parsing powers from data packs...");

		unparsedPowers.forEach((id, jsonWithSource) -> {

			PowerIdentifier powerId = PowerIdentifier.of(id);
			ResourceLocationUtil.setCurrent(id);

			MiscUtil.handleResult(
				wrappedParse(PowerHolder.CODEC.parse(ops, jsonWithSource.json())),
				powerHolder -> register(powersBuilder::put, powerHolder),
				warning -> LOGGER.warn("Found warnings while parsing {} from data pack [{}]: {}", powerId.asDisplayString(false), jsonWithSource.source(), warning),
				error -> LOGGER.error("Error trying to parse {} from data pack [{}] (skipping): {}", powerId.asDisplayString(false), jsonWithSource.source(), error)
			);

			ResourceLocationUtil.setCurrent(null);

		});

		powers = powersBuilder.build();
		PowerReloadEvents.AFTER.invoker().afterReload(manager, profiler);

		LOGGER.info("Finished parsing powers from data packs. Parsed {} power(s)", powers.size());
		pendingTags = ImmutableMap.copyOf(unparsedTags);

	}

	public static void init() {

	}

	private static void validate(ReloadableServerResources resources) {

		if (!powers.isEmpty()) {

			ImmutableMap.Builder<PowerIdentifier, PowerHolder<?>> builder = ImmutableMap.builder();
			int size = powers.size();

			LOGGER.info("Validating {} power(s)...", size);

			for (var powerHolder : powers.values()) {

				Power power = powerHolder.value();
				Reporter reporter = new Reporter("{\"" + powerHolder.id() + "\"}");

				Context.Validator validator = new Context.Validator(power.getType().requirements(), reporter).withResolver(MiscUtil.getLookupProvider(resources));
				power.validate(validator);

				reporter.getErrorsFlattened().ifPresentOrElse(
					error -> LOGGER.error("Found errors while validating {} {}", powerHolder.id().asDisplayString(false), error),
					() -> builder.put(powerHolder.id(), powerHolder)
				);

			}

			powers = builder.build();
			LOGGER.info("Finished validating {} power(s). Power manager contains {} power(s)", size, powers.size());

		}

		if (!pendingTags.isEmpty()) {

			LOGGER.info("Parsing power tags from data packs...");
			tags = ImmutableMap.copyOf(TAG_LOADER.build(pendingTags));

			LOGGER.info("Finished parsing power tags from data packs. Power manager contains {} power tag(s)", tags.size());
			pendingTags = ImmutableMap.of();

		}

	}

	private static DataResult<PowerHolder<?>> wrappedParse(DataResult<PowerHolder<?>> result) {
		return result.mapOrElse(
			DataResult::success,
			error -> {

				if (error.partialValue().isPresent()) {

					var partial = error.partialValue().get();

					if (partial.canBePartiallyParsed()) {
						return error;
					}

				}

				return DataResult.error(error.messageSupplier());

			}
		);
	}

	private static void send(RegistryAccess registryAccess, BiConsumer<Map<PowerIdentifier, Tag>, Map<ResourceLocation, List<PowerIdentifier>>> sender) {

		Map<PowerIdentifier, Tag> powers = new Object2ObjectLinkedOpenHashMap<>();
		Map<ResourceLocation, List<PowerIdentifier>> tags = new Object2ObjectLinkedOpenHashMap<>();

		RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);
		for (var entry : PowerManager.powers.entrySet()) {

			PowerIdentifier id = entry.getKey();
			PowerHolder<?> holder = entry.getValue();

			if (!id.isSubPower()) {
				PowerHolder.CODEC.encodeStart(ops, holder)
					.ifError(error -> LOGGER.error("Couldn't encode {} during the syncing process (skipping): {}", id.asDisplayString(false), error.message()))
					.ifSuccess(tag -> powers.put(id, tag));
			}

		}

		for (var tag : PowerManager.tags.entrySet()) {

			ResourceLocation tagId = tag.getKey();
			List<PowerHolder<?>> tagEntries = tag.getValue();

			for (var tagEntry : tagEntries) {
				tags.computeIfAbsent(tagId, k -> new ObjectArrayList<>()).add(tagEntry.id());
			}

		}

		sender.accept(powers, tags);

	}

	private static void onConfigure(ServerConfigurationPacketListenerImpl handler, MinecraftServer server) {

		if (ServerConfigurationNetworking.canSend(handler, ClientboundSyncInitiatedPacket.TYPE)) {
			send(server.registryAccess(), (powers, tags) -> handler.addTask(new SynchronizeTask(powers, tags)));
		}

	}

	private static void onReload(ServerPlayer player, boolean joined) {

		if (joined) {
			return;
		}

		send(
			player.registryAccess(),
			(powers, tags) -> {
				ServerPlayNetworking.send(player, new ClientboundUpdatePowersPacket(powers));
				ServerPlayNetworking.send(player, new ClientboundUpdateTagsPacket(tags));
			}
		);

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, ServerPowerManager::new);
		DependencyManager.POWERS.register(ID, dependencies -> dependencies.add(ActionManager.ID));

		ServerConfigurationConnectionEvents.CONFIGURE.addPhaseOrdering(ActionManager.ID, ID);
		ServerConfigurationConnectionEvents.CONFIGURE.register(ID, ServerPowerManager::onConfigure);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ActionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, ServerPowerManager::onReload);

		PowerPreparation.EVENT.addPhaseOrdering(ID, MultiplePower.ID);
		PowerPreparation.EVENT.register(ID, (id, jsonWithSource, directoryPath, ops) -> {

			if (jsonWithSource.json() instanceof JsonObject jsonObject) {
				jsonObject.addProperty(PowerHolder.ID_KEY, id.toString());
			}

		});

		PowerPreparation.EVENT.register(MultiplePower.ID, MultiplePower::preProcessSubPowers);
		ReloadableServerResourcesEvents.AFTER_LOAD.register(ID, ServerPowerManager::validate);

	}

}
