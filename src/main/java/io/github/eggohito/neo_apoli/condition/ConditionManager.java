package io.github.eggohito.neo_apoli.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public final class ConditionManager extends SimplePreparableReloadListener<Map<Condition.Kind<?>, Map<ResourceLocation, JsonWithSource>>> implements IdentifiableResourceReloadListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConditionManager.class);

	public static final ResourceLocation ID = NeoApoli.id("manager/conditions");
	public static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.CONDITIONS.invoker()::add).build();

	private static final Map<Condition.Kind<?>, Map<ResourceLocation, Condition>> BY_ID = new Object2ObjectOpenHashMap<>();
	private static final IdentityHashMap<Condition, ResourceLocation> BY_CONDITION = new IdentityHashMap<>();

	private final RegistryOps<JsonElement> ops;

	ConditionManager(HolderLookup.Provider wrapperLookup) {
		this.ops = wrapperLookup.createSerializationContext(JsonOps.INSTANCE);
	}

	@Override
	protected @NotNull Map<Condition.Kind<?>, Map<ResourceLocation, JsonWithSource>> prepare(ResourceManager manager, ProfilerFiller profiler) {

		Map<Condition.Kind<?>, Map<ResourceLocation, JsonWithSource>> result = new Object2ObjectOpenHashMap<>();
		NeoApoliRegistries.CONDITION_KIND.forEach(kind -> result
			.computeIfAbsent(kind, k -> new Object2ObjectOpenHashMap<>())
			.putAll(MiscUtil.collectJson(manager, JsonFileToIdConverter.registry(kind.registryKey()), ops, LOGGER::error)));

		return result;

	}

	@Override
	protected void apply(Map<Condition.Kind<?>, Map<ResourceLocation, JsonWithSource>> prepared, ResourceManager manager, ProfilerFiller profiler) {

		LOGGER.info("Parsing conditions from data packs...");
		BY_ID.clear();

		prepared.forEach((kind, conditions) -> conditions.forEach((id, jsonWithSource) -> {

			ResourceLocationUtil.setCurrent(id);
			kind.codec().parse(ops, jsonWithSource.json())
				.ifSuccess(condition -> register(id, condition))
				.ifError(error -> LOGGER.error("Error trying to parse {} \"{}\" from data pack [{}] (skipping): {}", kind.asDisplayString(false), id, jsonWithSource.source(), error.message()));

			ResourceLocationUtil.setCurrent(null);

		}));

		LOGGER.info("Finished parsing conditions from data packs. Parsed {} condition(s)", BY_ID.size());

	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}

	public static <C extends Condition> DataResult<C> getAsResult(Condition.Kind<C> kind, ResourceLocation id) {

		var entries = BY_ID.getOrDefault(kind, new Object2ObjectOpenHashMap<>());
		var matching = entries.get(id);

		if (matching != null) {
			return DataResult.success((C) matching);
		}

		else {
			return DataResult.error(() -> kind.asDisplayString() + " with ID \"" + id + "\" doesn't exist!");
		}

	}

	public static <C extends Condition> C get(Condition.Kind<C> kind, ResourceLocation id) {
		return getAsResult(kind, id).getOrThrow();
	}

	public static DataResult<ResourceLocation> getIdAsResult(Condition condition) {
		return containsId(condition)
			? DataResult.success(BY_CONDITION.get(condition))
			: DataResult.error(() -> condition + " doesn't correspond to any identifiers!");
	}

	public static ResourceLocation getId(Condition condition) {
		return getIdAsResult(condition).getOrThrow();
	}

	public static <C extends Condition> Stream<C> conditions(Condition.Kind<C> kind) {
		return BY_ID.getOrDefault(kind, new Object2ObjectOpenHashMap<>()).values()
			.stream()
			.map(condition -> (C) condition);
	}

	public static <C extends Condition> Stream<ResourceLocation> ids(Condition.Kind<C> kind) {
		return BY_ID.getOrDefault(kind, new Object2ObjectOpenHashMap<>()).keySet().stream();
	}

	public static boolean contains(Condition.Kind<?> kind, ResourceLocation id) {
		return BY_ID.getOrDefault(kind, new Object2ObjectOpenHashMap<>()).containsKey(id);
	}

	public static boolean containsId(Condition condition) {
		return BY_CONDITION.containsKey(condition);
	}

	public static void init() {

	}

	private static void register(ResourceLocation id, Condition condition) {
		BY_ID.computeIfAbsent(condition.getType().kind(), k -> new Object2ObjectOpenHashMap<>()).put(id, condition);
		BY_CONDITION.put(condition, id);
	}

	private static void sync(ServerPlayer recipient) {

		if (!recipient.server.isPublished()) {
			return;
		}

		LOGGER.info("Sent {} condition(s) to player {}!", BY_ID.size(), recipient.getName().getString());
		ServerPlayNetworking.send(recipient, new SynchronizeConditionsS2CPacket(BY_ID));

	}

	static {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, ConditionManager::new);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> sync(player));
	}

	public record SynchronizeConditionsS2CPacket(Map<Condition.Kind<?>, Map<ResourceLocation, Condition>> conditions) implements CustomPacketPayload {

		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Condition>> CONDITIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, Condition.STREAM_CODEC);
		private static final StreamCodec<RegistryFriendlyByteBuf, Map<Condition.Kind<?>, Map<ResourceLocation, Condition>>> KIND_CONDITIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, Condition.Kind.STREAM_CODEC, CONDITIONS_CODEC);

		public static final Type<SynchronizeConditionsS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_conditions"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeConditionsS2CPacket> CODEC = KIND_CONDITIONS_CODEC.map(SynchronizeConditionsS2CPacket::new, SynchronizeConditionsS2CPacket::conditions);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(Level level) {

			if (!level.isClientSide()) {
				return;
			}

			BY_ID.clear();
			BY_CONDITION.clear();

			conditions().forEach((kind, entries) -> entries.forEach(ConditionManager::register));

		}

	}

}
