package io.github.eggohito.neo_apoli.power.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.event.DependencyManager;
import io.github.eggohito.neo_apoli.api.event.PowerPreparation;
import io.github.eggohito.neo_apoli.api.event.PowerReloadEvents;
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
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.Profiler;
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
import java.util.function.BiConsumer;

public final class PowerManager extends AbstractContentAndTagManager<PowerIdentifier, PowerHolder<?>> implements IdentifiableResourceReloadListener {

	public static final TagEntry.Lookup<PowerHolder<?>> TAG_LOOKUP = new TagEntry.Lookup<>() {

		@Override
		public @Nullable PowerHolder<?> element(ResourceLocation id, boolean required) {
			return INSTANCE.getAsResult(PowerIdentifier.of(id)).result().orElse(null);
		}

		@Override
		public @Nullable Collection<PowerHolder<?>> tag(ResourceLocation id) {
			return INSTANCE.getTagAsResult(id).result().orElse(null);
		}

		@Override
		public String toString() {
			return "Power manager";
		}

	};

	public static final ResourceLocation ID = NeoApoli.id("manager/power");
	public static final PowerManager INSTANCE = new PowerManager();

	private static final Logger LOGGER = LoggerFactory.getLogger(PowerManager.class);
	private static final ImmutableSet<ResourceLocation> DEPENDENCIES = Util.make(ImmutableSet.builder(), DependencyManager.POWERS.invoker()::add).build();

	private final JsonFileToIdConverter contentLoader = JsonFileToIdConverter.registry(NeoApoliRegistryKeys.POWER);
	private final TagLoader<PowerHolder<?>> tagLoader = new TagLoader<>((id, required) -> this.getAsResult(PowerIdentifier.of(id)).result(), Registries.tagsDirPath(NeoApoliRegistryKeys.POWER));

	private volatile Map<ResourceLocation, JsonWithSource> pendingContents = Map.of();
	private volatile Map<ResourceLocation, List<TagLoader.EntryWithSource>> pendingTags = Map.of();

	private DynamicOps<JsonElement> ops;

	private PowerManager() {

	}

	@Override
	public @NotNull CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {

		CompletableFuture<Void> pendingPowers = CompletableFuture
			.runAsync(() -> this.preparePowers(manager), backgroundExecutor);
		CompletableFuture<Void> pendingTags = CompletableFuture
			.runAsync(() -> this.prepareTags(manager), backgroundExecutor);

		return CompletableFuture.allOf(pendingPowers, pendingTags)
			.thenCompose(barrier::wait)
			.thenAcceptAsync(unused -> this.applyPowers(manager), gameExecutor);

	}

	@Override
	public DataResult<PowerHolder<?>> getAsResult(PowerIdentifier key) {
		return this.getAsResult(key, k -> "Unknown " + k.asDisplayString(false));
	}

	@Override
	public DataResult<PowerIdentifier> getKeyAsResult(PowerHolder<?> value) {
		return this.getKeyAsResult(value, k -> "Unregistered power holder: " + value);
	}

	@Override
	public DataResult<List<PowerHolder<?>>> getTagAsResult(ResourceLocation id) {
		return this.getTagAsResult(id, i -> "Unknown power tag: \"" + i + "\"");
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public ImmutableSet<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}

	public DataResult<PowerIdentifier> getKeyAsResult(Power power) {

		for (var candidate : contents.values()) {

			if (candidate.value() == power) {
				return DataResult.success(candidate.id());
			}

		}

		return DataResult.error(() -> "Unregistered power: " + power);

	}

	public PowerIdentifier getKey(Power power) {
		return this.getKeyAsResult(power).getOrThrow();
	}

	public boolean containsKey(Power power) {
		return this.getKeyAsResult(power).isSuccess();
	}

	private void preparePowers(ResourceManager manager) {

		if (ops == null) {
			return;
		}

		var collected = MiscUtil.collectJson(manager, contentLoader, ops, LOGGER::error);
		collected.forEach((id, jsonWithSource) -> PowerPreparation.EVENT.invoker().prepare(id, jsonWithSource, contentLoader.directory(), ops));

		this.pendingContents = collected;

	}

	private void prepareTags(ResourceManager manager) {
		this.pendingTags = tagLoader.load(manager);
	}

	private void applyPowers(ResourceManager manager) {

		if (ops == null) {
			return;
		}

		LOGGER.info("Parsing powers from data packs...");
		PowerReloadEvents.BEFORE.invoker().beforeReload(manager, Profiler.get());

		ImmutableMap.Builder<PowerIdentifier, PowerHolder<?>> builder = ImmutableMap.builder();
		this.contents = ImmutableMap.of();

		pendingContents.forEach((id, jsonWithSource) -> {

			PowerIdentifier powerId = PowerIdentifier.of(id);
			ResourceLocationUtil.setCurrent(id);

			MiscUtil.handleResult(
				PowerHolder.CODEC.parse(ops, jsonWithSource.json()),
				holder -> handleSelfAndSubPowers(holder, builder::put),
				PowerHolder::canBePartiallyParsed,
				warning -> LOGGER.warn("Found warning(s) while parsing {} from data pack [{}]: {}", powerId.asDisplayString(false), jsonWithSource.source(), warning),
				error -> LOGGER.error("Error trying to parse {} from data pack [{}] (skipping): {}", powerId.asDisplayString(false), jsonWithSource.source(), error)
			);

			ResourceLocationUtil.setCurrent(null);

		});

		this.contents = builder.build();
		this.pendingContents = Map.of();

		LOGGER.info("Finished parsing powers from data packs. Parsed {} power(s)", contents.size());
		PowerReloadEvents.AFTER.invoker().afterReload(manager, Profiler.get());

	}

	private void applyTags() {

		LOGGER.info("Parsing power tags from data packs...");

		this.tags = ImmutableMap.copyOf(tagLoader.build(pendingTags));
		this.pendingTags = Map.of();

		LOGGER.info("Finished parsing power tags from data packs. Power manager contains {} power tag(s)", tags.size());

	}

	PowerManager withContext(@NotNull HolderLookup.Provider context) {
		this.ops = context.createSerializationContext(JsonOps.INSTANCE);
		return this;
	}

	void finalize(ReloadableServerResources resources) {

		if (!contents.isEmpty()) {

			ImmutableMap.Builder<PowerIdentifier, PowerHolder<?>> builder = ImmutableMap.builder();
			int size = contents.size();

			LOGGER.info("Validating {} power(s)...", size);

			for (var holder : contents.values()) {

				Power power = holder.value();
				Reporter reporter = new Reporter("{\"" + holder.id() + "\"}");

				Context.Validator validator = new Context.Validator(power.getType().requirements(), reporter).withResolver(MiscUtil.getLookupProvider(resources));
				power.validate(validator);

				reporter.getErrorsFlattened().ifPresentOrElse(
					error -> LOGGER.error("Found error(s) while validating {} {}", holder.id().asDisplayString(false), error),
					() -> builder.put(holder.id(), holder)
				);

			}

			this.contents = builder.build();
			LOGGER.info("Finished validating {} power(s). Power manager contains {} power(s)", size, contents.size());

		}

		this.applyTags();

	}

	void update(Map<PowerIdentifier, PowerHolder<?>> powers, Map<ResourceLocation, List<PowerHolder<?>>> tags) {
		this.contents = ImmutableMap.copyOf(powers);
		this.tags = ImmutableMap.copyOf(tags);
	}

	void send(ServerPlayer player, boolean joined) {

		if (!joined) {
			ServerPlayNetworking.send(player, new ClientboundUpdatePacket(contents, tags));
		}

	}

	private static void handleSelfAndSubPowers(PowerHolder<?> holder, BiConsumer<PowerIdentifier, PowerHolder<?>> handler) {

		handler.accept(holder.id(), holder);

		if (holder.value() instanceof MultiplePower(ImmutableSet<PowerHolder<?>> subPowers)) {
			subPowers.forEach(subPower -> handleSelfAndSubPowers(MultiplePower.validateNonRecursiveMultiple(subPower), handler));
		}

	}

	public record ClientboundUpdatePacket(Map<PowerIdentifier, PowerHolder<?>> powers, Map<ResourceLocation, List<PowerHolder<?>>> tags) implements CustomPacketPayload {

		public static final Type<ClientboundUpdatePacket> TYPE = new Type<>(ID.withPath(path -> "clientbound/" + path + "/update"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdatePacket> CODEC = StreamCodec.ofMember(ClientboundUpdatePacket::send, ClientboundUpdatePacket::receive);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		private static ClientboundUpdatePacket receive(RegistryFriendlyByteBuf buf) {

			Map<PowerIdentifier, PowerHolder<?>> powers = new Object2ObjectLinkedOpenHashMap<>();
			int powersCount = buf.readInt();

			for (int i = 0; i < powersCount; i++) {

				PowerIdentifier id = PowerIdentifier.STREAM_CODEC.decode(buf);

				try {
					handleSelfAndSubPowers(PowerHolder.STREAM_CODEC.decode(buf), powers::put);
				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't decode {} during the syncing process", id.asDisplayString(false), e);
					throw e;
				}

			}

			Map<ResourceLocation, List<PowerHolder<?>>> tags = new Object2ObjectLinkedOpenHashMap<>();
			int tagsCount = buf.readInt();

			for (int i = 0; i < tagsCount; i++) {

				ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
				int count = buf.readInt();

				for (int j = 0; j < count; j++) {

					try {

						PowerIdentifier holderId = PowerIdentifier.STREAM_CODEC.decode(buf);
						PowerHolder<?> holder = Objects.requireNonNull(powers.get(holderId), "Unknown " + holderId.asDisplayString(false));

						tags
							.computeIfAbsent(id, k -> new ObjectArrayList<>())
							.add(holder);

					}

					catch (Exception e) {
						NeoApoli.LOGGER.error("Couldn't decode power tag \"{}\" during the syncing process", id, e);
						throw e;
					}

				}

			}

			return new ClientboundUpdatePacket(powers, tags);

		}

		private void send(RegistryFriendlyByteBuf buf) {

			Map<PowerIdentifier, PowerHolder<?>> filtered = new Object2ObjectLinkedOpenHashMap<>(powers());
			filtered.keySet().removeIf(PowerIdentifier::isSubPower);

			buf.writeInt(filtered.size());

			for (var powerEntry : filtered.entrySet()) {

				PowerIdentifier id = powerEntry.getKey();
				PowerIdentifier.STREAM_CODEC.encode(buf, id);

				try {
					PowerHolder.STREAM_CODEC.encode(buf, powerEntry.getValue());
				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't encode {} during the syncing process", id.asDisplayString(false), e);
					throw e;
				}

			}

			buf.writeInt(tags().size());

			for (var tagEntry : tags().entrySet()) {

				ResourceLocation id = tagEntry.getKey();
				ResourceLocation.STREAM_CODEC.encode(buf, id);

				List<PowerHolder<?>> holders = tagEntry.getValue();
				buf.writeInt(holders.size());

				for (var holder : holders) {
					PowerIdentifier.STREAM_CODEC.encode(buf, holder.id());
				}

			}

		}

	}

}
