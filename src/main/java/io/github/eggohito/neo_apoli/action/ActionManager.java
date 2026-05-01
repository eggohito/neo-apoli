package io.github.eggohito.neo_apoli.action;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.api.event.ReloadableServerResourcesEvents;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public final class ActionManager implements IdentifiableResourceReloadListener {

	public static final ResourceLocation ID = NeoApoli.id("manager/actions");
	public static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.ACTIONS.invoker()::add).build();

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManager.class);

	private static final Map<ActionKind<?>, Map<ResourceLocation, Action>> BY_ID = new Object2ObjectOpenHashMap<>();
	private static final IdentityHashMap<Action, ResourceLocation> BY_ACTION = new IdentityHashMap<>();

	private static final Map<ActionKind<?>, Map<ResourceLocation, List<TagLoader.EntryWithSource>>> PREPARED_TAGS = new Object2ObjectOpenHashMap<>();
	private static final Map<ActionKind<?>, Map<ResourceLocation, List<Action>>> TAGS = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> ops;

	ActionManager(HolderLookup.Provider wrapperLookup) {
		this.ops = wrapperLookup.createSerializationContext(JsonOps.INSTANCE);
	}

	@Override
	public @NotNull CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {

		CompletableFuture<Map<ActionKind<?>, Map<ResourceLocation, JsonWithSource>>> preparedElementsFuture = CompletableFuture
			.supplyAsync(() -> prepareElements(manager, Profiler.get()), backgroundExecutor);
		CompletableFuture<Void> preparedTagsFuture = CompletableFuture
			.runAsync(() -> prepareTags(manager, Profiler.get()), backgroundExecutor);

		return preparedTagsFuture.thenCombine(preparedElementsFuture, Pair::of)
			.thenCompose(barrier::wait)
			.thenAcceptAsync(preparedTagsAndElements -> this.applyElements(preparedTagsAndElements.getSecond(), manager, Profiler.get()), gameExecutor);

	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}

	private Map<ActionKind<?>, Map<ResourceLocation, JsonWithSource>> prepareElements(ResourceManager manager, ProfilerFiller ignoredProfiler) {

		Map<ActionKind<?>, Map<ResourceLocation, JsonWithSource>> result = new Object2ObjectOpenHashMap<>();
		NeoApoliRegistries.ACTION_KIND.forEach(kind -> result
			.computeIfAbsent(kind, k -> new Object2ObjectOpenHashMap<>())
			.putAll(MiscUtil.collectJson(manager, JsonFileToIdConverter.registry(kind.registryKey()), ops, LOGGER::error)));

		return result;

	}

	private void applyElements(Map<ActionKind<?>, Map<ResourceLocation, JsonWithSource>> prepared, ResourceManager ignoredManager, ProfilerFiller ignoredProfiler) {

		LOGGER.info("Parsing actions from data packs...");
		BY_ID.clear();

		prepared.forEach((kind, actions) -> actions.forEach((id, jsonWithSource) -> {

			ResourceLocationUtil.setCurrent(id);
			kind.codec().parse(ops, jsonWithSource.json())
				.ifSuccess(action -> register(id, action))
				.ifError(error -> LOGGER.error("Error trying to parse {} \"{}\" from data pack [{}] (skipping): {}", kind.asDisplayString(false), id, jsonWithSource.source(), error.message()));

			ResourceLocationUtil.setCurrent(null);

		}));

		StringBuilder message = new StringBuilder("Finished parsing actions from data packs. Parsed " + BY_ID.size() + " action(s) in total;");
		BY_ID.forEach((kind, entries) -> message.append("\n\t - Parsed ").append(entries.size()).append(" ").append(kind.asDisplayString(false)).append(" tag(s)"));

		LOGGER.info(message.toString());

	}

	public static <A extends Action> DataResult<A> getAsResult(ActionKind<A> kind, ResourceLocation id) {

		var actions = BY_ID.getOrDefault(kind, new Object2ObjectOpenHashMap<>());
		var matching = actions.get(id);

		if (matching != null) {
			return DataResult.success((A) matching);
		}

		else {
			return DataResult.error(() -> kind.asDisplayString() + " with ID \"" + id + "\" doesn't exist!");
		}

	}

	public static <A extends Action> A get(ActionKind<A> kind, ResourceLocation id) {
		return getAsResult(kind, id).getOrThrow();
	}

	public static <A extends Action> DataResult<ResourceLocation> getIdAsResult(A action) {
		return Optional.ofNullable(BY_ACTION.get(action))
			.map(DataResult::success)
			.orElse(DataResult.error(() -> action + " doesn't correspond to any identifiers!"));
	}

	public static ResourceLocation getId(Action action) {
		return getIdAsResult(action).getOrThrow();
	}

	public static <A extends Action> DataResult<List<A>> getAllFromTag(ActionKind<A> kind, TagKey<A> tag) {
		return getAllFromTag(kind, tag.location());
	}

	public static <A extends Action> DataResult<List<A>> getAllFromTag(ActionKind<A> kind, ResourceLocation tagId) {

		var entries = TAGS.getOrDefault(kind, new Object2ObjectOpenHashMap<>());
		var matching = entries.get(tagId);

		if (matching != null) {
			return DataResult.success((List<A>) matching);
		}

		else {
			return DataResult.error(() -> "Unknown action tag: \"" + tagId + "\"");
		}

	}

	public static <A extends Action> Stream<A> actions(ActionKind<A> kind) {
		return BY_ID.getOrDefault(kind, new Object2ObjectOpenHashMap<>()).values()
			.stream()
			.map(action -> (A) action);
	}

	public static <A extends Action> Stream<ResourceLocation> ids(ActionKind<A> kind) {
		return BY_ID.getOrDefault(kind, new Object2ObjectOpenHashMap<>()).keySet().stream();
	}

	public static <A extends Action> boolean contains(ActionKind<A> kind, ResourceLocation id) {
		return BY_ID.getOrDefault(kind, new Object2ObjectOpenHashMap<>()).containsKey(id);
	}

	public static <A extends Action> boolean containsId(A action) {
		return BY_ACTION.containsKey(action);
	}

	@ApiStatus.Internal
	public static void init() {

	}

	private static void prepareTags(ResourceManager manager, ProfilerFiller ignoredProfiler) {

		PREPARED_TAGS.clear();

		for (var kind : NeoApoliRegistries.ACTION_KIND) {

			String directory = Registries.tagsDirPath(kind.registryKey());
			TagLoader<Action> tagLoader = new TagLoader<>((id, required) -> getAsResult(kind, id).result(), directory);

			var entries = tagLoader.load(manager);

			if (!entries.isEmpty()) {
				PREPARED_TAGS.computeIfAbsent(kind, k -> new Object2ObjectOpenHashMap<>()).putAll(entries);
			}

		}

	}

	private static void applyTags() {

		if (PREPARED_TAGS.isEmpty()) {
			return;
		}

		LOGGER.info("Parsing action tags from data packs...");
		TAGS.clear();

		PREPARED_TAGS.forEach((kind, entries) -> {

			String directory = Registries.tagsDirPath(kind.registryKey());
			TagLoader<Action> tagLoader = new TagLoader<>((id, required) -> getAsResult(kind, id).result(), directory);

			TAGS
				.computeIfAbsent(kind, k -> new Object2ObjectOpenHashMap<>())
				.putAll(tagLoader.build(entries));

		});

		StringBuilder message = new StringBuilder("Finished parsing action tags from data packs. Parsed " + TAGS.size() + " action tag(s) in total;");
		TAGS.forEach((kind, entries) -> message.append("\n\t - Parsed ").append(entries.size()).append(" ").append(kind.asDisplayString(false)).append(" tag(s)"));

		LOGGER.info(message.toString());
		PREPARED_TAGS.clear();

	}

	private static void register(ResourceLocation id, Action action) {
		BY_ID.computeIfAbsent(action.getType().kind(), k -> new Object2ObjectOpenHashMap<>()).put(id, action);
		BY_ACTION.put(action, id);
	}

	private static void sync(ServerPlayer recipient) {

		if (!recipient.server.isPublished()) {
			return;
		}

		LOGGER.info("Sent {} action(s) to player {}!", BY_ID.size(), recipient.getName().getString());
		ServerPlayNetworking.send(recipient, new SynchronizeS2CPacket(BY_ID));

		LOGGER.info("Sent {} action tag(s) to player {}!", TAGS.size(), recipient.getName().getString());
		ServerPlayNetworking.send(recipient, new SynchronizeTagsS2CPacket(TAGS));

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, ActionManager::new);
		DependencyManager.ACTIONS.register(ID, dependencies -> dependencies.add(ConditionManager.ID));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ConditionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sync(player));

		ReloadableServerResourcesEvents.AFTER_LOAD.register(ID, ignored -> applyTags());

	}

	public record SynchronizeS2CPacket(Map<ActionKind<?>, Map<ResourceLocation, Action>> actions) implements CustomPacketPayload {

		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Action>> ACTIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, Action.STREAM_CODEC);
		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ActionKind<?>, Map<ResourceLocation, Action>>> KIND_ACTIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ActionKind.STREAM_CODEC, ACTIONS_CODEC);

		public static final Type<SynchronizeS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_actions"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeS2CPacket> CODEC = KIND_ACTIONS_CODEC.map(SynchronizeS2CPacket::new, SynchronizeS2CPacket::actions);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(Level level) {

			if (!level.isClientSide()) {
				return;
			}

			BY_ID.clear();
			BY_ACTION.clear();

			actions().forEach((kind, entries) -> entries.forEach(ActionManager::register));

		}

	}

	public record SynchronizeTagsS2CPacket(Map<ActionKind<?>, Map<ResourceLocation, List<Action>>> tags) implements CustomPacketPayload {

		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, List<Action>>> TAGS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.collection(ObjectArrayList::new, Action.STREAM_CODEC));
		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ActionKind<?>, Map<ResourceLocation, List<Action>>>> KIND_TAGS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ActionKind.STREAM_CODEC, TAGS_CODEC);

		public static final Type<SynchronizeTagsS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_action_tags"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeTagsS2CPacket> CODEC = KIND_TAGS_CODEC.map(SynchronizeTagsS2CPacket::new, SynchronizeTagsS2CPacket::tags);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(Level level) {

			if (!level.isClientSide()) {
				return;
			}

			TAGS.clear();
			tags().forEach((kind, entries) -> TAGS
				.computeIfAbsent(kind, k -> new Object2ObjectOpenHashMap<>())
				.putAll(entries));

		}

	}

}
