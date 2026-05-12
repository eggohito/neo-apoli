package io.github.eggohito.neo_apoli.power;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.api.event.PowerPreparation;
import io.github.eggohito.neo_apoli.api.event.PowerReloadEvents;
import io.github.eggohito.neo_apoli.api.event.ReloadableServerResourcesEvents;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
import net.minecraft.server.ReloadableServerResources;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class PowerManager implements IdentifiableResourceReloadListener {

	public static final ResourceLocation ID = NeoApoli.id("manager/powers");
	public static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.POWERS.invoker()::add).build();

	public static final TagEntry.Lookup<PowerHolder<?>> TAG_LOOKUP = new TagEntry.Lookup<>() {

		@Nullable
		@Override
		public PowerHolder<?> element(ResourceLocation id, boolean required) {
			return getAsResult(PowerIdentifier.of(id)).result().orElse(null);
		}

		@Nullable
		@Override
		public Collection<PowerHolder<?>> tag(ResourceLocation id) {
			return getAllFromTag(id).result().orElse(null);
		}

		@Override
		public String toString() {
			return "Power manager";
		}

	};

	private static final Logger LOGGER = LoggerFactory.getLogger(PowerManager.class);

	private static final TagLoader<PowerHolder<?>> TAG_LOADER = new TagLoader<>((id, required) -> getAsResult(PowerIdentifier.of(id)).result(), Registries.tagsDirPath(NeoApoliRegistryKeys.POWER));
	private static final JsonFileToIdConverter ELEMENT_LOADER = JsonFileToIdConverter.registry(NeoApoliRegistryKeys.POWER);

	private static final Object2ObjectOpenHashMap<PowerIdentifier, PowerHolder<?>> BY_ID = new Object2ObjectOpenHashMap<>();
	private static final Map<Power, PowerIdentifier> BY_POWER = new IdentityHashMap<>();

	private static final Object2ObjectOpenHashMap<ResourceLocation, List<TagLoader.EntryWithSource>> POSTPONED_TAGS = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectOpenHashMap<ResourceLocation, List<PowerHolder<?>>> TAGS = new Object2ObjectOpenHashMap<>();

	private final RegistryOps<JsonElement> ops;

	PowerManager(HolderLookup.Provider wrapperLookup) {
		this.ops = wrapperLookup.createSerializationContext(JsonOps.INSTANCE);
	}

	@Override
	public @NotNull CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {

		CompletableFuture<Map<ResourceLocation, JsonWithSource>> preparedElementsFuture = CompletableFuture
			.supplyAsync(() -> prepareElements(manager, Profiler.get()), backgroundExecutor);
		CompletableFuture<Void> preparedTagsFuture = CompletableFuture
			.runAsync(() -> prepareTags(manager, Profiler.get()), backgroundExecutor);

		return preparedTagsFuture.thenCombine(preparedElementsFuture, Pair::of)
			.thenCompose(barrier::wait)
			.thenAcceptAsync(unusedAndElements -> this.applyElements(unusedAndElements.getSecond(), manager, Profiler.get()), gameExecutor);

	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}

	private Map<ResourceLocation, JsonWithSource> prepareElements(ResourceManager manager, ProfilerFiller ignoredProfiler) {

		Map<ResourceLocation, JsonWithSource> prepared = MiscUtil.collectJson(manager, ELEMENT_LOADER, ops, LOGGER::error);
		prepared.forEach((resourceLocation, jsonWithSource) -> PowerPreparation.EVENT.invoker().prepare(resourceLocation, jsonWithSource, ELEMENT_LOADER.directory(), ops));

		return prepared;

	}

	private void applyElements(Map<ResourceLocation, JsonWithSource> prepared, ResourceManager manager, ProfilerFiller profiler) {

		PowerReloadEvents.BEFORE.invoker().beforeReload(manager, profiler);

		LOGGER.info("Parsing powers from data packs...");
		startLoading();

		prepared.forEach((id, elementWithSource) -> {

			ResourceLocationUtil.setCurrent(id);
			MiscUtil.handleResult(
				PowerHolder.CODEC.parse(ops, elementWithSource.json()),
				PowerManager::register,
				warning -> LOGGER.warn("Found warnings while parsing power {} from data pack [{}]: {}", id, elementWithSource.source(), warning),
				error -> LOGGER.error("Error trying to parse power {} from data pack [{}] (skipping): {}", id, elementWithSource.source(), error)
			);

			ResourceLocationUtil.setCurrent(null);

		});

		LOGGER.info("Finished parsing powers from data packs. Parsed {} power(s)", BY_ID.size());
		endLoading();

		PowerReloadEvents.AFTER.invoker().afterReload(manager, profiler);

	}

	@ApiStatus.Internal
	public static void init() {

	}

	public static DataResult<List<PowerHolder<?>>> getAllFromTag(TagKey<PowerHolder<?>> tag) {
		return getAllFromTag(tag.location());
	}

	public static DataResult<List<PowerHolder<?>>> getAllFromTag(ResourceLocation tagId) {
		return Optional.ofNullable(TAGS.get(tagId))
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error(() -> "Unknown power tag: " + tagId));
	}

	public static DataResult<PowerHolder<?>> getAsResult(PowerIdentifier id) {
		return contains(id)
			? DataResult.success(BY_ID.get(id))
			: DataResult.error(() -> "Referenced " + id.asDisplayString(false) + " doesn't exist!");
	}

	public static PowerHolder<?> get(PowerIdentifier id) {
		return getAsResult(id).getOrThrow(IllegalArgumentException::new);
	}

	public static DataResult<PowerIdentifier> getIdAsResult(Power power) {
		return containsId(power)
			? DataResult.success(BY_POWER.get(power))
			: DataResult.error(() -> power + " doesn't correspond to any IDs!");
	}

	public static PowerIdentifier getId(Power power) {
		return getIdAsResult(power).getOrThrow(IllegalArgumentException::new);
	}

	public static Set<ResourceLocation> tags() {
		return TAGS.keySet();
	}

	public static Set<PowerIdentifier> ids() {
		return new ObjectOpenHashSet<>(BY_ID.keySet());
	}

	public static Collection<PowerHolder<?>> powers() {
		return new ObjectOpenHashSet<>(BY_ID.values());
	}

	public static boolean contains(PowerIdentifier id) {
		return BY_ID.containsKey(id);
	}

	public static boolean containsId(Power power) {
		return BY_POWER.containsKey(power);
	}

	private static void startLoading() {
		BY_ID.clear();
		BY_POWER.clear();
	}

	private static void endLoading() {
		BY_ID.trim();
	}

	private static void prepareTags(ResourceManager manager, ProfilerFiller ignoredProfiler) {

		POSTPONED_TAGS.clear();
		Map<ResourceLocation, List<TagLoader.EntryWithSource>> pendingTags = TAG_LOADER.load(manager);

		POSTPONED_TAGS.putAll(pendingTags);
		POSTPONED_TAGS.trim();

	}

	private static void applyTags() {

		if (POSTPONED_TAGS.isEmpty()) {
			return;
		}

		LOGGER.info("Parsing power tags from data packs...");
		TAGS.clear();

		TAGS.putAll(TAG_LOADER.build(POSTPONED_TAGS));
		LOGGER.info("Finished parsing power tags from data packs. Parsed {} power tag(s)", TAGS.size());

		POSTPONED_TAGS.clear();
		TAGS.trim();

	}

	private static <P extends Power> void register(PowerHolder<P> powerHolder) {

		PowerIdentifier powerId = powerHolder.id();
		Power power = powerHolder.value();

		BY_ID.put(powerId, powerHolder);
		BY_POWER.put(power, powerId);

		if (power instanceof MultiplePower multiplePower) {

			if (powerId.isSubPower()) {
				throw new IllegalStateException("Tried to register \"" + powerId.asDisplayString(false) + " with \"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, NeoApoliPowerTypes.MULTIPLE) + "\" power type, which is not allowed!");
			}

			else {
				multiplePower.getSubPowers().forEach(PowerManager::register);
			}

		}

	}

	private static void validate(ReloadableServerResources resources) {

		if (BY_ID.isEmpty()) {
			return;
		}

		ObjectIterator<PowerHolder<?>> iterator = BY_ID.values().iterator();
		int size = BY_ID.size();

		LOGGER.info("Validating {} power(s)...", size);

		while (iterator.hasNext()) {

			PowerHolder<?> powerHolder = iterator.next();
			Power power = powerHolder.value();

			Reporter reporter = new Reporter("{\"" + powerHolder.id() + "\"}");
			Context.Validator validator = new Context.Validator(power.getType().keySet(), reporter).withResolver(MiscUtil.getLookupProvider(resources));

			power.validate(validator);
			reporter.getErrorsFlattened().ifPresent(error -> {

				LOGGER.error("Found errors while validating {} {}", powerHolder.id().asDisplayString(false), error);

				BY_POWER.remove(power);
				iterator.remove();

			});

		}

		LOGGER.info("Finished validating {} power(s). Power manager contains {} power(s)", size, BY_ID.size());
		BY_ID.trim();

	}

	private static void sync(ServerPlayer recipient) {

		if (!recipient.server.isPublished()) {
			return;
		}

		Set<PowerHolder<?>> filtered = BY_ID.values()
			.stream()
			.filter(Predicate.not(PowerHolder::isSubPower))
			.collect(Collectors.toCollection(ObjectOpenHashSet::new));

		LOGGER.info("Sent {} power(s) to player {}!", filtered.size(), recipient.getName().getString());
		ServerPlayNetworking.send(recipient, new SynchronizeS2CPacket(filtered));

		LOGGER.info("Sent {} power tag(s) to player {}!", TAGS.size(), recipient.getName().getString());
		ServerPlayNetworking.send(recipient, new SynchronizeTagsS2CPacket(TAGS));

	}

	static {

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, PowerManager::new);
		DependencyManager.POWERS.register(ID, dependencies -> dependencies.add(ActionManager.ID));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(ActionManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sync(player));

		PowerPreparation.EVENT.addPhaseOrdering(ID, MultiplePower.ID);

		PowerPreparation.EVENT.register(MultiplePower.ID, MultiplePower::preProcessSubPowers);
		PowerPreparation.EVENT.register(ID, (id, jsonWithSource, directoryPath, ops) -> {

			if (jsonWithSource.json() instanceof JsonObject jsonObject) {
				jsonObject.addProperty(PowerHolder.ID_KEY, id.toString());
			}

		});

		ReloadableServerResourcesEvents.AFTER_LOAD.addPhaseOrdering(ActionManager.ID, ID);
		ReloadableServerResourcesEvents.AFTER_LOAD.register(ID, resources -> {
			validate(resources);
			applyTags();
		});

	}

	public record SynchronizeS2CPacket(Set<PowerHolder<?>> powers) implements CustomPacketPayload {

		private static final StreamCodec<RegistryFriendlyByteBuf, List<PowerHolder<?>>> LIST_CODEC = ByteBufCodecs.collection(ObjectArrayList::new, PowerHolder.STREAM_CODEC);
		private static final StreamCodec<RegistryFriendlyByteBuf, Set<PowerHolder<?>>> SET_CODEC = LIST_CODEC.map(ObjectOpenHashSet::new, ObjectArrayList::new);

		public static final Type<SynchronizeS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_powers"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeS2CPacket> CODEC = SET_CODEC.map(SynchronizeS2CPacket::new, SynchronizeS2CPacket::powers);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(Level level) {

			if (!level.isClientSide()) {
				return;
			}

			startLoading();
			powers().forEach(PowerManager::register);
			endLoading();

		}

	}

	public record SynchronizeTagsS2CPacket(Map<ResourceLocation, List<PowerHolder<?>>> tags) implements CustomPacketPayload {

		private static final StreamCodec<ByteBuf, PowerHolder<?>> ENTRY_CODEC = PowerIdentifier.STREAM_CODEC.map(PowerManager::get, PowerHolder::id);
		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, List<PowerHolder<?>>>> TAGS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.collection(ObjectArrayList::new, ENTRY_CODEC));

		public static final Type<SynchronizeTagsS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_power_tags"));
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
