package io.github.eggohito.neo_apoli.condition.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import io.github.eggohito.neo_apoli.util.manager.AbstractContentManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ConditionManager extends AbstractContentManager<ResourceLocation, Condition> implements IdentifiableResourceReloadListener {

	public static final ResourceLocation ID = NeoApoli.id("manager/condition");
	public static final ConditionManager INSTANCE = new ConditionManager();

	private static final Logger LOGGER = LoggerFactory.getLogger(ConditionManager.class);
	private static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.CONDITIONS.invoker()::add).build();

	private final JsonFileToIdConverter contentLoader = JsonFileToIdConverter.registry(NeoApoliRegistryKeys.CONDITION);
	private DynamicOps<JsonElement> ops;

	private ConditionManager() {

	}

	@Override
	public @NotNull CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {
		return CompletableFuture.supplyAsync(() -> this.prepareConditions(manager), backgroundExecutor)
			.thenCompose(barrier::wait)
			.thenAcceptAsync(this::applyConditions, gameExecutor);
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public ImmutableSet<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}

	ConditionManager withContext(@NotNull HolderLookup.Provider context) {
		this.ops = context.createSerializationContext(JsonOps.INSTANCE);
		return this;
	}

	Map<ResourceLocation, JsonWithSource> prepareConditions(ResourceManager manager) {

		if (ops == null) {
			return Map.of();
		}

		else {
			return MiscUtil.collectJson(manager, contentLoader, ops, LOGGER::error);
		}

	}

	void applyConditions(Map<ResourceLocation, JsonWithSource> pending) {

		if (ops == null) {
			return;
		}

		LOGGER.info("Parsing conditions from data packs...");
		ImmutableMap.Builder<ResourceLocation, Condition> builder = ImmutableMap.builder();

		pending.forEach((id, jsonWithSource) -> {

			ResourceLocationUtil.setCurrent(id);
			MiscUtil.handleResult(
				Condition.CODEC.parse(ops, jsonWithSource.json()),
				condition -> builder.put(id, condition),
				warning -> LOGGER.warn("Found warning(s) while parsing condition \"{}\" from data pack [{}]: {}", id, jsonWithSource.source(), warning),
				error -> LOGGER.error("Error trying to parse condition \"{}\" from data pack [{}] (skipping): {}", id, jsonWithSource.source(), error)
			);

			ResourceLocationUtil.setCurrent(null);

		});

		this.contents = builder.build();
		LOGGER.info("Finished parsing conditions from data packs. Condition manager contains {} condition(s)", contents.size());

	}

	void update(Map<ResourceLocation, Condition> conditions) {
		this.contents = ImmutableMap.copyOf(conditions);
	}

	void send(ServerPlayer player, boolean joined) {

		if (!joined) {
			ServerPlayNetworking.send(player, new ClientboundUpdatePacket(contents));
		}

	}

	public record ClientboundUpdatePacket(Map<ResourceLocation, Condition> conditions) implements CustomPacketPayload {

		public static final Type<ClientboundUpdatePacket> TYPE = new Type<>(ID.withPath(path -> "clientbound/" + path + "/update"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdatePacket> CODEC = StreamCodec.ofMember(ClientboundUpdatePacket::send, ClientboundUpdatePacket::receive);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		private static ClientboundUpdatePacket receive(RegistryFriendlyByteBuf buf) {
			
			Map<ResourceLocation, Condition> conditions = new Object2ObjectLinkedOpenHashMap<>();
			int count = buf.readInt();

			for (int i = 0; i < count; i++) {

				ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);

				try {
					conditions.put(id, Condition.STREAM_CODEC.decode(buf));
				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't decode condition \"{}\" during the syncing process", id, e);
					throw e;
				}
				
			}

			return new ClientboundUpdatePacket(conditions);
			
		}

		private void send(RegistryFriendlyByteBuf buf) {

			buf.writeInt(conditions().size());
			
			for (var entry : conditions().entrySet()) {
				
				ResourceLocation.STREAM_CODEC.encode(buf, entry.getKey());
				
				try {
					Condition.STREAM_CODEC.encode(buf, entry.getValue());
				}
				
				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't encode condition \"{}\" during the syncing process", entry.getKey(), e);
					throw e;
				}
				
			}
			
		}

	}

}
