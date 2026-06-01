package io.github.eggohito.neo_apoli.action.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public final class ServerActionManager extends ActionManager implements IdentifiableResourceReloadListener {

	private static final TagLoader<Action> TAG_LOADER = new TagLoader<>((id, required) -> getAsResult(id).result(), Registries.tagsDirPath(NeoApoliRegistryKeys.ACTION));
	private static final JsonFileToIdConverter JSON_LOADER = JsonFileToIdConverter.registry(NeoApoliRegistryKeys.ACTION);

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerActionManager.class);
	private static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.ACTIONS.invoker()::add).build();

	private final DynamicOps<JsonElement> ops;

	ServerActionManager(HolderLookup.Provider provider) {
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

		CompletableFuture<Map<ResourceLocation, JsonWithSource>> actionsFuture = CompletableFuture
			.supplyAsync(() -> MiscUtil.collectJson(manager, JSON_LOADER, ops, LOGGER::error), backgroundExecutor);
		CompletableFuture<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> tagsFuture = CompletableFuture
			.supplyAsync(() -> TAG_LOADER.load(manager), backgroundExecutor);

		return actionsFuture.thenCombine(tagsFuture, Pair::of)
			.thenCompose(barrier::wait)
			.thenAcceptAsync(pair -> this.applyAll(pair.getFirst(), pair.getSecond()), gameExecutor);

	}

	private void applyAll(Map<ResourceLocation, JsonWithSource> unparsedActions, Map<ResourceLocation, List<TagLoader.EntryWithSource>> unparsedTags) {

		LOGGER.info("Parsing actions from data packs...");
		ImmutableMap.Builder<ResourceLocation, Action> parsedActions = ImmutableMap.builder();

		unparsedActions.forEach((id, jsonWithSource) -> {

			ResourceLocationUtil.setCurrent(id);
			MiscUtil.handleResult(
				Action.CODEC.parse(ops, jsonWithSource.json()),
				action -> parsedActions.put(id, action),
				warning -> LOGGER.warn("Found warnings while parsing action \"{}\" from data pack [{}]: {}", id, jsonWithSource.source(), warning),
				error -> LOGGER.error("Error trying to parse action \"{}\" from data pack [{}] (skipping): {}", id, jsonWithSource.source(), error)
			);

			ResourceLocationUtil.setCurrent(null);

		});

		actions = parsedActions.build();
		LOGGER.info("Finished parsing actions from data packs. Action manager contains {} action(s)", actions.size());

		LOGGER.info("Parsing action tags from data packs...");
		Map<ResourceLocation, List<Action>> parsedTags = TAG_LOADER.build(unparsedTags);

		tags = ImmutableMap.copyOf(parsedTags);
		LOGGER.info("Finished parsing action tags from data packs. Action manager contains {} action tag(s)", tags.size());

	}

	public static void init() {

	}

	private static void send(RegistryAccess registryAccess, BiConsumer<Map<ResourceLocation, Tag>, Map<ResourceLocation, List<ResourceLocation>>> sender) {

		Map<ResourceLocation, Tag> actions = new Object2ObjectLinkedOpenHashMap<>();
		Map<ResourceLocation, List<ResourceLocation>> tags = new Object2ObjectLinkedOpenHashMap<>();

		RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);
		for (var entry : ActionManager.actions.entrySet()) {

			ResourceLocation id = entry.getKey();
			Action action = entry.getValue();

			Action.CODEC.encodeStart(ops, action)
				.ifError(error -> LOGGER.error("Couldn't encode action \"{}\" during the syncing process (skipping): {}", id, error.message()))
				.ifSuccess(tag -> actions.put(id, tag));

		}

		for (var tag : ActionManager.tags.entrySet()) {

			ResourceLocation tagId = tag.getKey();
			List<Action> tagEntries = tag.getValue();

			for (var tagEntry : tagEntries) {
				getIdAsResult(tagEntry).ifSuccess(id -> tags
					.computeIfAbsent(tagId, k -> new ObjectArrayList<>())
					.add(id));
			}

		}

		sender.accept(actions, tags);

	}

	private static void onConfigure(ServerConfigurationPacketListenerImpl handler, MinecraftServer server) {

		if (ServerConfigurationNetworking.canSend(handler, ClientboundSyncInitiatedPacket.TYPE)) {
			send(server.registryAccess(), (actions, tags) -> handler.addTask(new SynchronizeTask(actions, tags)));
		}

	}

	private static void onReload(ServerPlayer player, boolean joined) {

		if (!joined) {
			return;
		}

		send(
			player.registryAccess(),
			(actions, tags) -> {
				ServerPlayNetworking.send(player, new ClientboundUpdateActionsPacket(actions));
				ServerPlayNetworking.send(player, new ClientboundUpdateTagsPacket(tags));
			}
		);

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, ServerActionManager::new);
		DependencyManager.ACTIONS.register(ID, dependencies -> dependencies.add(ConditionManager.ID));

		ServerConfigurationConnectionEvents.CONFIGURE.addPhaseOrdering(ConditionManager.ID, ID);
		ServerConfigurationConnectionEvents.CONFIGURE.register(ID, ServerActionManager::onConfigure);

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ConditionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, ServerActionManager::onReload);

	}

}
