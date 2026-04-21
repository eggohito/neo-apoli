package io.github.eggohito.neo_apoli.action;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.api.event.ReloadableServerResourcesEvents;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonReloadListener;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public final class ActionManager implements JsonReloadListener {

	public static final ResourceLocation ID = NeoApoli.id("manager/actions");
	public static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.ACTIONS.invoker()::add).build();

	public static final TagEntry.Lookup<Action> TAG_LOOKUP =  new TagEntry.Lookup<>() {

		@Nullable
		@Override
		public Action element(ResourceLocation id, boolean required) {
			return getAsResult(id).result().orElse(null);
		}

		@Nullable
		@Override
		public Collection<Action> tag(ResourceLocation id) {
			return getAllFromTag(id).result().orElse(null);
		}

		@Override
		public String toString() {
			return "Action manager";
		}

	};

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManager.class);

	private static final TagLoader<Action> TAG_LOADER = new TagLoader<>((id, required) -> getAsResult(id).result(), Registries.tagsDirPath(NeoApoliRegistryKeys.ACTION));
	private static final JsonFileToIdConverter ELEMENT_LOADER = JsonFileToIdConverter.registry(NeoApoliRegistryKeys.ACTION);

	private static final Object2ObjectOpenHashMap<ResourceLocation, Action> BY_ID = new Object2ObjectOpenHashMap<>();
	private static final IdentityHashMap<Action, ResourceLocation> BY_ACTION = new IdentityHashMap<>();

	private static final Object2ObjectOpenHashMap<ResourceLocation, List<TagLoader.EntryWithSource>> PREPARED_TAGS = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<ResourceLocation, List<Action>> TAGS = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> ops;

	ActionManager(HolderLookup.Provider wrapperLookup) {
		this.ops = wrapperLookup.createSerializationContext(JsonOps.INSTANCE);
	}

	@Override
	public @NotNull CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {

		CompletableFuture<Map<ResourceLocation, JsonWithSource>> preparedElementsFuture = CompletableFuture
			.supplyAsync(() -> MiscUtil.collectJson(manager, ELEMENT_LOADER, ops, LOGGER::error), backgroundExecutor);
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

	private void applyElements(Map<ResourceLocation, JsonWithSource> prepared, ResourceManager ignoredManager, ProfilerFiller ignoredProfiler) {

		LOGGER.info("Parsing actions from data packs...");
		BY_ID.clear();

		prepared.forEach((id, entry) -> {

			ResourceLocationUtil.setCurrent(id);
			Action.CODEC.parse(ops, entry.json())
				.ifSuccess(action -> register(id, action))
				.ifError(error -> LOGGER.error("Error trying to parse action \"{}\" from data pack [{}] (skipping): {}", id, entry.source(), error.message()));

			ResourceLocationUtil.setCurrent(null);

		});

		LOGGER.info("Finished parsing actions from data packs. Parsed {} action(s)", BY_ID.size());

	}

	public static DataResult<Action> getAsResult(ResourceLocation id) {
		return contains(id)
			? DataResult.success(BY_ID.get(id))
			: DataResult.error(() -> "Action with ID \"" + id + "\" does not exist!");
	}

	public static Action get(ResourceLocation id) {
		return getAsResult(id).getOrThrow();
	}

	public static DataResult<ResourceLocation> getIdAsResult(Action action) {
		return containsId(action)
			? DataResult.success(BY_ACTION.get(action))
			: DataResult.error(() -> action + " doesn't correspond to any identifiers!");
	}

	public static ResourceLocation getId(Action action) {
		return getIdAsResult(action).getOrThrow();
	}

	public static DataResult<List<Action>> getAllFromTag(TagKey<Action> tag) {
		return getAllFromTag(tag.location());
	}

	public static DataResult<List<Action>> getAllFromTag(ResourceLocation tagId) {
		return Optional.ofNullable(TAGS.get(tagId))
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error(() -> "Unknown action tag: " + tagId));
	}

	public static Stream<Action> actions() {
		return BY_ID.values().stream();
	}

	public static Stream<ResourceLocation> ids() {
		return BY_ID.keySet().stream();
	}

	public static boolean contains(ResourceLocation id) {
		return BY_ID.containsKey(id);
	}

	public static boolean containsId(Action action) {
		return BY_ACTION.containsKey(action);
	}

	@ApiStatus.Internal
	public static void init() {

	}

	private static void prepareTags(ResourceManager manager, ProfilerFiller ignoredProfiler) {

		PREPARED_TAGS.clear();
		Map<ResourceLocation, List<TagLoader.EntryWithSource>> pendingTags = TAG_LOADER.load(manager);

		PREPARED_TAGS.putAll(pendingTags);
		PREPARED_TAGS.trim();

	}

	private static void applyTags() {

		if (PREPARED_TAGS.isEmpty()) {
			return;
		}

		LOGGER.info("Parsing action tags from data packs...");
		TAGS.clear();

		TAGS.putAll(TAG_LOADER.build(PREPARED_TAGS));
		LOGGER.info("Finished parsing action tags from data packs. Parsed {} action tag(s)", TAGS.size());

		PREPARED_TAGS.clear();
		TAGS.trim();

	}

	private static void register(ResourceLocation id, Action action) {
		BY_ID.put(id, action);
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

	public record SynchronizeS2CPacket(Map<ResourceLocation, Action> actions) implements CustomPacketPayload {

		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Action>> ACTIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, Action.STREAM_CODEC);

		public static final Type<SynchronizeS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_actions"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeS2CPacket> CODEC = ACTIONS_CODEC.map(SynchronizeS2CPacket::new, SynchronizeS2CPacket::actions);

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

			actions().forEach(ActionManager::register);

			BY_ID.trim();

		}

	}

	public record SynchronizeTagsS2CPacket(Map<ResourceLocation, List<Action>> tags) implements CustomPacketPayload {

		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, List<Action>>> TAGS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.collection(ObjectArrayList::new, Action.STREAM_CODEC));

		public static final Type<SynchronizeTagsS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_action_tags"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeTagsS2CPacket> CODEC = TAGS_CODEC.map(SynchronizeTagsS2CPacket::new, SynchronizeTagsS2CPacket::tags);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(Level level) {

			if (!level.isClientSide()) {
				return;
			}

			TAGS.clear();
			TAGS.putAll(tags());

			TAGS.trim();

		}

	}

}
