package io.github.eggohito.neo_apoli.action.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionHolder;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import io.github.eggohito.neo_apoli.util.manager.AbstractContentAndTagManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ActionManager extends AbstractContentAndTagManager<ResourceLocation, ActionHolder<?>> implements IdentifiableResourceReloadListener {

	public static final TagEntry.Lookup<ActionHolder<?>> TAG_LOOKUP = new TagEntry.Lookup<>() {

		@Override
		public @Nullable ActionHolder<?> element(ResourceLocation id, boolean required) {
			return INSTANCE.getAsResult(id).result().orElse(null);
		}

		@Override
		public @Nullable Collection<ActionHolder<?>> tag(ResourceLocation id) {
			return INSTANCE.getTagAsResult(id).result().orElse(null);
		}

		@Override
		public String toString() {
			return "Action manager";
		}

	};

	public static final ResourceLocation ID = NeoApoli.id("manager/action");
	public static final ActionManager INSTANCE = new ActionManager();

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManager.class);
	private static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.ACTIONS.invoker()::add).build();

	private final JsonFileToIdConverter contentLoader = JsonFileToIdConverter.registry(NeoApoliRegistryKeys.ACTION);
	private final TagLoader<ActionHolder<?>> tagLoader = new TagLoader<>((id, required) -> this.getAsResult(id).result(), Registries.tagsDirPath(NeoApoliRegistryKeys.ACTION));

	private volatile Map<ResourceLocation, JsonWithSource> pendingContents = Map.of();
	private volatile Map<ResourceLocation, List<TagLoader.EntryWithSource>> pendingTags = Map.of();

	private DynamicOps<JsonElement> ops;

	private ActionManager() {

	}

	@Override
	public @NotNull CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {

		CompletableFuture<Void> pendingActions = CompletableFuture
			.runAsync(() -> this.prepareActions(manager), backgroundExecutor);
		CompletableFuture<Void> pendingTags = CompletableFuture
			.runAsync(() -> this.prepareTags(manager), backgroundExecutor);

		return CompletableFuture.allOf(pendingActions, pendingTags)
			.thenCompose(barrier::wait)
			.thenAcceptAsync(unused -> this.applyAll(), gameExecutor);

	}

	@Override
	public DataResult<ActionHolder<?>> getAsResult(ResourceLocation key) {
		return this.getAsResult(key, k -> "Unknown action: \"" + k + "\"");
	}

	@Override
	public DataResult<ResourceLocation> getKeyAsResult(ActionHolder<?> value) {
		return this.getKeyAsResult(value, v -> "Unregistered action: " + value);
	}

	@Override
	public DataResult<List<ActionHolder<?>>> getTagAsResult(ResourceLocation id) {
		return this.getTagAsResult(id, i -> "Unknown action tag: \"" + i + "\"");
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public ImmutableSet<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}

	public DataResult<ResourceLocation> getKeyAsResult(Action action) {

		for (var candidate : contents.values()) {

			if (candidate.value() == action) {
				return DataResult.success(candidate.id());
			}

		}

		return DataResult.error(() -> "Unregistered action: " + action);

	}

	public ResourceLocation getKey(Action action) {
		return this.getKeyAsResult(action).getOrThrow();
	}

	ActionManager withContext(@NotNull HolderLookup.Provider context) {
		this.ops = context.createSerializationContext(JsonOps.INSTANCE);
		return this;
	}

	void prepareActions(ResourceManager manager) {

		if (ops != null) {
			this.pendingContents = MiscUtil.collectJson(manager, contentLoader, ops, LOGGER::error);
		}

	}

	void prepareTags(ResourceManager manager) {
		this.pendingTags = tagLoader.load(manager);
	}

	void applyActions() {

		if (ops == null) {
			return;
		}

		LOGGER.info("Parsing actions from data packs...");

		ImmutableMap.Builder<ResourceLocation, ActionHolder<?>> builder = ImmutableMap.builder();
		this.contents = ImmutableMap.of();

		pendingContents.forEach((id, jsonWithSource) -> {

			ResourceLocationUtil.setCurrent(id);
			MiscUtil.handleResult(
				Action.CODEC.parse(ops, jsonWithSource.json()),
				action -> builder.put(id, new ActionHolder<>(id, action)),
				warning -> LOGGER.warn("Found warning(s) while parsing action \"{}\" from data pack [{}]: {}", id, jsonWithSource.source(), warning),
				error -> LOGGER.error("Error trying to parse action \"{}\" from data pack [{}] (skipping): {}", id, jsonWithSource.source(), error)
			);

			ResourceLocationUtil.setCurrent(null);

		});

		this.contents = builder.build();
		this.pendingContents = Map.of();

		LOGGER.info("Finished parsing actions from data packs. Action manager contains {} action(s)", contents.size());

	}

	void applyTags() {

		LOGGER.info("Parsing action tags from data packs...");

		this.tags = ImmutableMap.copyOf(tagLoader.build(pendingTags));
		this.pendingTags = Map.of();

		LOGGER.info("Finished parsing action tags from data packs. Action manager contains {} action tag(s)", tags.size());

	}

	void applyAll() {
		this.applyActions();
		this.applyTags();
	}

	void update(Map<ResourceLocation, ActionHolder<?>> actions, Map<ResourceLocation, List<ActionHolder<?>>> tags) {
		this.contents = ImmutableMap.copyOf(actions);
		this.tags = ImmutableMap.copyOf(tags);
	}

	void send(ServerPlayer player, boolean joined) {

		if (!joined) {
			ServerPlayNetworking.send(player, new ClientboundUpdatePacket(contents, tags));
		}

	}

	public record ClientboundUpdatePacket(Map<ResourceLocation, ActionHolder<?>> actions, Map<ResourceLocation, List<ActionHolder<?>>> tags) implements CustomPacketPayload {

		public static final Type<ClientboundUpdatePacket> TYPE = new Type<>(ID.withPath(path -> "clientbound/" + path + "/update"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdatePacket> CODEC = StreamCodec.ofMember(ClientboundUpdatePacket::send, ClientboundUpdatePacket::receive);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		private static ClientboundUpdatePacket receive(RegistryFriendlyByteBuf buf) {

			Map<ResourceLocation, ActionHolder<?>> powers = new Object2ObjectLinkedOpenHashMap<>();
			int powersCount = buf.readInt();

			for (int i = 0; i < powersCount; i++) {

				ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);

				try {
					powers.put(id, ActionHolder.STREAM_CODEC.decode(buf));
				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't decode {} during the syncing process", id, e);
					throw e;
				}

			}

			Map<ResourceLocation, List<ActionHolder<?>>> tags = new Object2ObjectLinkedOpenHashMap<>();
			int tagsCount = buf.readInt();

			for (int i = 0; i < tagsCount; i++) {

				ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
				int count = buf.readInt();

				for (int j = 0; j < count; j++) {

					try {

						ResourceLocation holderId = ResourceLocation.STREAM_CODEC.decode(buf);
						ActionHolder<?> holder = Objects.requireNonNull(powers.get(holderId), "Unknown " + holderId);

						tags
							.computeIfAbsent(id, k -> new ObjectArrayList<>())
							.add(holder);

					}

					catch (Exception e) {
						NeoApoli.LOGGER.error("Couldn't decode action tag \"{}\" during the syncing process", id, e);
						throw e;
					}

				}

			}

			return new ClientboundUpdatePacket(powers, tags);

		}

		private void send(RegistryFriendlyByteBuf buf) {

			buf.writeInt(actions().size());

			for (var actionEntry : actions().entrySet()) {

				ResourceLocation id = actionEntry.getKey();
				ResourceLocation.STREAM_CODEC.encode(buf, id);

				try {
					ActionHolder.STREAM_CODEC.encode(buf, actionEntry.getValue());
				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't encode action \"{}\" during the syncing process", id, e);
					throw e;
				}

			}

			buf.writeInt(tags().size());

			for (var tagEntry : tags().entrySet()) {

				ResourceLocation id = tagEntry.getKey();
				ResourceLocation.STREAM_CODEC.encode(buf, id);

				List<ActionHolder<?>> holders = tagEntry.getValue();
				buf.writeInt(holders.size());

				for (var holder : holders) {
					ResourceLocation.STREAM_CODEC.encode(buf, holder.id());
				}

			}

		}

	}

}
