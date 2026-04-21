package io.github.eggohito.neo_apoli.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonReloadListener;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

public final class ConditionManager extends SimplePreparableReloadListener<Map<ResourceLocation, JsonWithSource>> implements JsonReloadListener {

	private static final JsonFileToIdConverter LOADER = JsonFileToIdConverter.registry(NeoApoliRegistryKeys.CONDITION);
	private static final Logger LOGGER = LoggerFactory.getLogger(ConditionManager.class);

	public static final ResourceLocation ID = NeoApoli.id("manager/conditions");
	public static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.CONDITIONS.invoker()::add).build();

	private static final Object2ObjectOpenHashMap<ResourceLocation, Condition> BY_ID = new Object2ObjectOpenHashMap<>();
	private static final IdentityHashMap<Condition, ResourceLocation> BY_CONDITION = new IdentityHashMap<>();

	private final RegistryOps<JsonElement> ops;

	ConditionManager(HolderLookup.Provider wrapperLookup) {
		this.ops = wrapperLookup.createSerializationContext(JsonOps.INSTANCE);
	}

	@Override
	protected @NotNull Map<ResourceLocation, JsonWithSource> prepare(ResourceManager manager, ProfilerFiller profiler) {
		return MiscUtil.collectJson(manager, LOADER, ops, LOGGER::error);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonWithSource> prepared, ResourceManager manager, ProfilerFiller profiler) {

		LOGGER.info("Parsing conditions from data packs...");
		BY_ID.clear();

		prepared.forEach((id, entry) -> {

			ResourceLocationUtil.setCurrent(id);
			Condition.CODEC.parse(ops, entry.json())
				.ifSuccess(condition -> register(id, condition))
				.ifError(error -> LOGGER.error("Error trying to parse condition \"{}\" from data pack [{}] (skipping): {}", id, entry.source(), error.message()));

			ResourceLocationUtil.setCurrent(null);

		});

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

	public static void init() {

	}

	private static void register(ResourceLocation id, Condition condition) {
		BY_ID.put(id, condition);
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

	public record SynchronizeConditionsS2CPacket(Map<ResourceLocation, Condition> conditions) implements CustomPacketPayload {

		private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Condition>> CONDITIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, Condition.STREAM_CODEC);

		public static final Type<SynchronizeConditionsS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_conditions"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeConditionsS2CPacket> CODEC = CONDITIONS_CODEC.map(SynchronizeConditionsS2CPacket::new, SynchronizeConditionsS2CPacket::conditions);

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

			conditions().forEach(ConditionManager::register);

			BY_ID.trim();

		}

	}

}
