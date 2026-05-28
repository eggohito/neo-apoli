package io.github.eggohito.neo_apoli.condition.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ServerConditionManager extends ConditionManager implements IdentifiableResourceReloadListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerConditionManager.class);
	private static final JsonFileToIdConverter JSON_LOADER = JsonFileToIdConverter.registry(NeoApoliRegistryKeys.CONDITION);
	private static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.CONDITIONS.invoker()::add).build();

	private final DynamicOps<JsonElement> ops;

	ServerConditionManager(HolderLookup.Provider provider) {
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
		return CompletableFuture.supplyAsync(() -> MiscUtil.collectJson(manager, JSON_LOADER, ops, LOGGER::error), backgroundExecutor)
			.thenCompose(barrier::wait)
			.thenAcceptAsync(this::apply, gameExecutor);
	}

	private void apply(Map<ResourceLocation, JsonWithSource> unparsed) {

		LOGGER.info("Parsing conditions from data packs...");
		ImmutableMap.Builder<ResourceLocation, Condition> builder = ImmutableMap.builder();

		unparsed.forEach((id, jsonWithSource) -> {

			ResourceLocationUtil.setCurrent(id);
			MiscUtil.handleResult(
				Condition.CODEC.parse(ops, jsonWithSource.json()),
				condition -> builder.put(id, condition),
				warning -> LOGGER.warn("Found warnings while parsing condition \"{}\" from data pack [{}]: {}", id, jsonWithSource.source(), warning),
				error -> LOGGER.error("Error trying to parse condition \"{}\" from data pack [{}] (skipping): {}", id, jsonWithSource.source(), error)
			);

			ResourceLocationUtil.setCurrent(null);

		});

		conditions = builder.build();
		LOGGER.info("Finished parsing conditions from data packs. Condition manager contains {} condition(s)", conditions.size());

	}

	public static void send(ServerPlayer recipient) {

		if (!recipient.server.isPublished()) {
			return;
		}

		LOGGER.info("Sent {} condition(s) to player {}!", conditions.size(), recipient.getName().getString());
		ServerPlayNetworking.send(recipient, new ClientboundConditionsUpdatePacket(conditions));

	}

	public static void init() {

	}

	static {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ID, ServerConditionManager::new);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> send(player));
	}

}
