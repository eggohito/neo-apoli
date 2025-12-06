package io.github.eggohito.neo_apoli.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

@SuppressWarnings("UnstableApiUsage")
public final class PowersComponent implements Component, AutoSyncedComponent, CommonTickingComponent, RespawnableComponent<PowersComponent> {

	private static final ResourceLocation ID = NeoApoli.id("powers");

	private static final int FULL_SYNC_ID = 0;
	private static final int GRANT_SYNC_ID = 1;
	private static final int REVOKE_SYNC_ID = 2;

	private final Map<PowerReference, Power.Instance<?>> instances;
	private final Map<PowerReference, Set<ResourceLocation>> sources;

	private final Entity holder;

	public PowersComponent(Entity holder) {
		this.instances = new ConcurrentHashMap<>();
		this.sources = new ConcurrentHashMap<>();
		this.holder = holder;
	}

	@Override
	public void writeToNbt(CompoundTag rootNbt, HolderLookup.Provider wrapperLookup) {

		RegistryOps<Tag> nbtOps = wrapperLookup.createSerializationContext(NbtOps.INSTANCE);
		ListTag powersNbt = new ListTag();

		this.instances.forEach((powerReference, instance) -> {

			Set<ResourceLocation> sources = this.sources.getOrDefault(powerReference, Set.of());
			Tag data = instance.encodeData(nbtOps)
				.mapError(error -> "Error trying to encode data of " + powerReference.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (defaulting to empty NBT): " + error)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.orElseGet(nbtOps::emptyMap);

			Entry<Tag> entry = new Entry<>(powerReference, instance.getPower().getType(), sources, new Dynamic<>(nbtOps, data));
			Entry.CODEC.encoder().encodeStart(nbtOps, entry)
				.mapError(error -> "Error trying to encode " + powerReference.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (skipping): " + error)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.ifPresent(powersNbt::add);

		});

		rootNbt.put("powers", powersNbt);

	}

	@Override
	public void readFromNbt(CompoundTag rootNbt, HolderLookup.Provider wrapperLookup) {

		RegistryOps<Tag> nbtOps = wrapperLookup.createSerializationContext(NbtOps.INSTANCE);

		ListTag powersNbt = rootNbt.getListOrEmpty("powers");
		ListIterator<Tag> powersNbtIterator = powersNbt.listIterator();

		this.instances.clear();
		this.sources.clear();

		while (powersNbtIterator.hasNext()) {

			int index = powersNbtIterator.nextIndex();
			Tag powerNbt = powersNbtIterator.next();

			try {

				Entry<?> entry = Entry.CODEC.decoder()
					.parse(nbtOps, powerNbt)
					.getOrThrow();

				PowerReference powerReference = entry.powerReference();
				DataResult<Power> powerResult = PowerManager.getAsResult(powerReference);

				switch (powerResult) {
					case DataResult.Success<Power> success -> {

						Power power = success.value();
						Dynamic<Tag> data = entry.data().convert(nbtOps);

						Power.Instance<?> instance = power.createInstance(holder);
						Set<ResourceLocation> sources = entry.sources();

						if (Objects.equals(entry.type(), power.getType())) {
							instance.decodeData(nbtOps, data.getValue())
								.mapError(error -> "Error decoding data of " + powerReference.asDisplayString(false) + " from NBT (skipping): " + error)
								.error()
								.map(DataResult.Error::message)
								.ifPresent(NeoApoli.LOGGER::warn);
						}

						else {
							NeoApoli.LOGGER.warn("Power instance of {} has changed. Its data won't be recovered!", powerReference.asDisplayString(false));
						}

						this.instances.put(powerReference, instance);
						this.sources.put(powerReference, sources);

					}
					case DataResult.Error<Power> error ->
						NeoApoli.LOGGER.warn("Error decoding {} from cardinal_components.\"{}\".powers[{}] of entity {} (skipping): {}", powerReference.asDisplayString(false), NeoApoliEntityComponents.POWERS.getId(), index, holder.getName().getString(), error.message());
				}

			}

			catch (Exception e) {
				NeoApoli.LOGGER.warn("Error decoding power NBT element ({}) at cardinal_components.\"{}\".powers[{}] (skipping): {}", powerNbt, NeoApoliEntityComponents.POWERS.getId(), index, e);
			}

		}

	}

	@Override
	public void writeSyncPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient) {
		buf.writeVarInt(FULL_SYNC_ID);
		AutoSyncedComponent.super.writeSyncPacket(buf, recipient);
	}

	@Override
	public void applySyncPacket(RegistryFriendlyByteBuf buf) {

		int syncId = buf.readVarInt();

		switch (syncId) {
			case FULL_SYNC_ID ->
				AutoSyncedComponent.super.applySyncPacket(buf);
			case GRANT_SYNC_ID ->
				Synchronizer.GRANT.receive(buf, this);
			case REVOKE_SYNC_ID ->
				Synchronizer.REVOKE.receive(buf, this);
			default ->
				NeoApoli.LOGGER.warn("Entity {} (UUID: {}) received powers component sync packet with unknown sync ID (Expected 0-2, received {})!", holder.getName().getString(), holder.getStringUUID(), syncId);
		}

	}

	@Override
	public void tick() {

		for (var instance : instances.values()) {

			if (instance.shouldTick()) {
				instance.onTick();
			}

		}

	}

	@Override
	public boolean shouldCopyForRespawn(boolean lossless, boolean keepInventory, boolean sameCharacter) {
		return true;
	}

	public boolean grantPower(PowerReference reference, ResourceLocation source) {
		return !holder.level().isClientSide()
			&& grantPowerSideAgnostic(reference, source);
	}

	private boolean grantPowerSideAgnostic(PowerReference reference, ResourceLocation source) {
		return PowerManager.getEntryAsResult(reference)
			.mapError(error -> "Error trying to grant " + reference.asDisplayString(false) + " from source '" + source + "' to entity " + (holder.getName().getString() + " (UUID: " + holder.getStringUUID() + ")") + " (skipping): " + error)
			.resultOrPartial(NeoApoli.LOGGER::warn)
			.map(entry -> grantPower(entry, source))
			.orElse(false);
	}

	private boolean grantPower(PowerEntry<?> entry, ResourceLocation source) {

		List<Power.Instance<?>> addedPowers = new ObjectArrayList<>();
		List<Power.Instance<?>> grantedPowers = new ObjectArrayList<>();

		boolean granted = this.grantPower(entry, source, addedPowers::add, grantedPowers::add);

		addedPowers.forEach(Power.Instance::onAdded);
		grantedPowers.forEach(Power.Instance::onGranted);

		return granted;

	}

	private boolean grantPower(PowerEntry<?> entry, ResourceLocation source, Consumer<Power.Instance<?>> addedAction, Consumer<Power.Instance<?>> grantedAction) {

		PowerReference reference = entry.reference();
		Set<ResourceLocation> sources = this.sources.computeIfAbsent(reference, k -> new ObjectOpenHashSet<>());

		if (sources.contains(source)) {
			return false;
		}

		Power power = entry.power();
		Power.Instance<?> instance = power.createInstance(holder);

		sources.add(source);
		addedAction.accept(instance);

		if (!instances.containsKey(reference)) {
			grantedAction.accept(instance);
		}

		this.instances.put(reference, instance);
		if (power instanceof MultiplePower multiplePower) {

			for (PowerEntry<?> subPower : multiplePower.getSubPowers()) {
				PowerManager.getEntryAsResult(subPower.reference()).ifSuccess(realSubPower -> this.grantPower(realSubPower, source, addedAction, grantedAction));
			}

		}

		return true;

	}

	public boolean revokePower(PowerReference id, ResourceLocation source) {
		return !holder.level().isClientSide()
			&& revokePowerSideAgnostic(id, source);
	}

	private boolean revokePowerSideAgnostic(PowerReference id, ResourceLocation source) {

		List<PowerReference> revokedPowers = new ObjectArrayList<>();
		boolean result = this.revokePower(id, source, revokedPowers::add);

		instances.keySet().removeIf(revokedPowers::contains);
		sources.keySet().removeIf(revokedPowers::contains);

		return result;

	}

	private boolean revokePower(PowerReference id, ResourceLocation source, Consumer<PowerReference> onRevokedCallback) {

		Set<ResourceLocation> sources = this.sources.getOrDefault(id, new ObjectOpenHashSet<>());
		boolean removed = sources.remove(source);

		if (removed && instances.containsKey(id)) {

			Power.Instance<?> instance = instances.get(id);
			instance.onRemoved();

			if (sources.isEmpty()) {
				instance.onRevoked();
				onRevokedCallback.accept(id);
			}

			if (instance.getPower() instanceof MultiplePower multiplePower) {

				for (var subPower : multiplePower.getSubPowers()) {
					PowerManager.getEntryAsResult(subPower.reference()).ifSuccess(realSubPower -> this.revokePower(realSubPower.reference(), source, onRevokedCallback));
				}

			}

			return true;

		}

		return false;

	}

	public void forEach(TriConsumer<PowerReference, Power.Instance<?>, Set<ResourceLocation>> consumer, BooleanSupplier continueCondition) {

		for (var entry : this.instances.entrySet()) {

			PowerReference reference = entry.getKey();
			Set<ResourceLocation> sources = this.sources.getOrDefault(reference, new ObjectOpenHashSet<>());

			if (!sources.isEmpty()) {

				consumer.accept(reference, entry.getValue(), sources);

				if (!continueCondition.getAsBoolean()) {
					break;
				}

			}

		}

	}

	public void forEach(TriConsumer<PowerReference, Power.Instance<?>, Set<ResourceLocation>> consumer) {
		this.forEach(consumer, () -> true);
	}

	public List<Power.Instance<?>> getAllInstances() {

		List<Power.Instance<?>> collected = new ObjectArrayList<>();
		this.forEach((reference, type, sources) -> collected.add(type));

		return collected;

	}

	public <I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass) {
		return this.getInstances(instanceClass, type -> true);
	}

	public <I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass, Predicate<I> instanceFilter) {

		List<I> collected = new ObjectArrayList<>();
		this.forEach((reference, type, sources) -> {

			if (instanceClass.isInstance(type)) {

				I castedInstance = instanceClass.cast(type);

				if (instanceFilter.test(castedInstance)) {
					collected.add(castedInstance);
				}

			}

		});

		return collected;

	}

	public List<Power> getAll() {
		return this.getAll(true);
	}

	public List<Power> getAll(boolean includingSubPowers) {

		List<Power> collected = new ObjectArrayList<>();
		this.forEach((reference, type, sources) -> {

			if (includingSubPowers || !reference.subPower()) {
				collected.add(type.getPower());
			}

		});

		return collected;

	}

	public List<Power> getAllFromSource(ResourceLocation source) {

		List<Power> collected = new ObjectArrayList<>();
		this.forEach((reference, instance, sources) -> {

			if (sources.contains(source)) {
				collected.add(instance.getPower());
			}

		});

		return collected;

	}

	@NotNull
	public Power.Instance<?> getInstance(PowerReference reference) {
		return Objects.requireNonNull(this.getNullableInstance(reference), "Entity " + holder.getName().getString() + " didn't have " + reference.asDisplayString(false) + " granted!");
	}

	@Nullable
	public Power.Instance<?> getNullableInstance(PowerReference reference) {
		return instances.get(reference);
	}

	public boolean hasInstance(PowerReference reference) {
		return instances.containsKey(reference)
			&& sources.containsKey(reference);
	}

	public boolean hasInstance(PowerReference reference, ResourceLocation source) {
		return hasInstance(reference)
			&& sources.get(reference).contains(source);
	}

	public <I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass) {
		return this.hasInstances(instanceClass, instance -> true);
	}

	public <I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass, Predicate<I> instanceFilter) {

		MutableBoolean result = new MutableBoolean(false);
		TriConsumer<PowerReference, Power.Instance<?>, Set<ResourceLocation>> consumer = (powerReference, instance, sources) -> {

			if (instanceClass.isInstance(instance) && instanceFilter.test(instanceClass.cast(instance))) {
				result.setTrue();
			}

		};

		this.forEach(consumer, result::isFalse);
		return result.isTrue();

	}

	public Set<ResourceLocation> getSources(PowerReference reference) {
		return sources.containsKey(reference)
			? new ObjectOpenHashSet<>(sources.get(reference))
			: ObjectOpenHashSet.of();
	}

	public Set<PowerReference> getReferences(ResourceLocation source) {

		Set<PowerReference> collected = new ObjectOpenHashSet<>();
		this.forEach((reference, instance, sources) -> {

			if (sources.contains(source)) {
				collected.add(reference);
			}

		});

		return collected;

	}

	public static void forEach(Entity holder, TriConsumer<PowerReference, Power.Instance<?>, Set<ResourceLocation>> consumer, BooleanSupplier continueCondition) {
		NeoApoliEntityComponents.POWERS.maybeGet(holder).ifPresent(powersComponent -> powersComponent.forEach(consumer, continueCondition));
	}

	public static void forEach(Entity holder, TriConsumer<PowerReference, Power.Instance<?>, Set<ResourceLocation>> consumer) {
		NeoApoliEntityComponents.POWERS.maybeGet(holder).ifPresent(powersComponent -> powersComponent.forEach(consumer));
	}

	public static <I extends Power.Instance<?>> boolean hasInstances(Entity entity, Class<I> implClass) {
		return NeoApoliEntityComponents.POWERS.maybeGet(entity)
			.stream()
			.anyMatch(powersComponent -> powersComponent.hasInstances(implClass));
	}

	public static <I extends Power.Instance<?>> boolean hasInstances(Entity entity, Class<I> implClass, Predicate<I> implFilter) {
		return NeoApoliEntityComponents.POWERS.maybeGet(entity)
			.stream()
			.anyMatch(powersComponent -> powersComponent.hasInstances(implClass, implFilter));
	}

	public static List<Power.Instance<?>> getAllInstances(Entity entity) {
		return NeoApoliEntityComponents.POWERS.maybeGet(entity)
			.map(PowersComponent::getAllInstances)
			.orElseGet(ObjectArrayList::new);
	}

	public static <I extends Power.Instance<?>> List<I> getInstances(Entity entity, Class<I> implClass) {
		return NeoApoliEntityComponents.POWERS.maybeGet(entity)
			.map(powersComponent -> powersComponent.getInstances(implClass))
			.orElseGet(ObjectArrayList::new);
	}

	public static <I extends Power.Instance<?>> List<I> getInstances(Entity entity, Class<I> implClass, Predicate<I> implFilter) {
		return NeoApoliEntityComponents.POWERS.maybeGet(entity)
			.map(powersComponent -> powersComponent.getInstances(implClass, implFilter))
			.orElseGet(ObjectArrayList::new);
	}

	public static ResourceLocation getId() {
		return ID;
	}

	private static void update(Entity entity, boolean initialize) {

		PowersComponent component = NeoApoliEntityComponents.POWERS.get(entity);
		Set<Map.Entry<PowerReference, Power.Instance<?>>> entries = new ObjectOpenHashSet<>(component.instances.entrySet());

		int mismatches = 0;

		for (var entry : entries) {

			PowerReference reference = entry.getKey();
			Set<ResourceLocation> sources = component.getSources(reference);

			if (!PowerManager.contains(reference)) {

				NeoApoli.LOGGER.error("Removed unregistered {} from entity {}!", reference.asDisplayString(false), entity.getName().getString());

				for (var source : sources) {
					component.revokePower(reference, source);
				}

			}

			else {

				Power oldPower = entry.getValue().getPower();
				Power newPower = PowerManager.get(reference);

				if (!Objects.equals(oldPower, newPower)) {

					NeoApoli.LOGGER.warn("{} from entity {} has mismatched data! Updating...", reference.asDisplayString(), entity.getName().getString());
					mismatches++;

					for (var source : sources) {
						component.revokePower(reference, source);
						component.grantPower(reference, source);
					}

					Power.Instance<?> oldInstance = entry.getValue();
					Power.Instance<?> newInstance = component.getInstance(reference);

					if (oldInstance.getClass().isInstance(newInstance)) {

						RegistryOps<Tag> nbtOps = entity.registryAccess().createSerializationContext(NbtOps.INSTANCE);
						DataResult<Tag> data = oldInstance.encodeData(nbtOps);

						switch (data) {
							case DataResult.Success<Tag> success ->
								newInstance.decodeData(nbtOps, success.value())
									.ifSuccess(unit -> NeoApoli.LOGGER.info("Successfully migrated old data of {}!", reference.asDisplayString(false)))
									.ifError(error -> NeoApoli.LOGGER.warn("Couldn't decode old data of {} during migration: {}", reference.asDisplayString(false), error.message()));
							case DataResult.Error<Tag> error ->
								NeoApoli.LOGGER.warn("Couldn't encode old data of {} during migration: {}", reference.asDisplayString(false), error.message());
						}

					}

					else {
						NeoApoli.LOGGER.warn("Couldn't migrate old data of {}, as it's using a different power type!", reference.asDisplayString(false));
					}

				}

			}

		}

		if (mismatches > 0) {
			NeoApoli.LOGGER.info("Finished updating {} power(s) with mismatched data from entity {}!", mismatches, entity.getName().getString());
		}

		NeoApoliEntityComponents.POWERS.sync(entity);

	}

	static {

		ServerEntityEvents.ENTITY_LOAD.register(getId(), (entity, world) -> {

			if (!(entity instanceof Player)) {
				update(entity, false);
			}

		});

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(PowerManager.ID, getId());
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(getId(), PowersComponent::update);

	}

	public record Entry<T>(PowerReference powerReference, PowerType<?> type, Set<ResourceLocation> sources, Dynamic<T> data) {

		public static final MapCodec<Entry<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PowerReference.CODEC.fieldOf("id").forGetter(Entry::powerReference),
			PowerType.CODEC.fieldOf("type").forGetter(Entry::type),
			NeoApoliCodecs.MUTABLE_NON_EMPTY_IDENTIFIER_SET.fieldOf("sources").forGetter(Entry::sources),
			Codec.PASSTHROUGH.fieldOf("data").forGetter(Entry::data)
		).apply(instance, Entry::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Entry<?>> STREAM_CODEC = StreamCodec.composite(
			PowerReference.STREAM_CODEC, Entry::powerReference,
			PowerType.STREAM_CODEC, Entry::type,
			NeoApoliStreamCodecs.MUTABLE_NON_EMPTY_IDENTIFIER_SET, Entry::sources,
			NeoApoliStreamCodecs.REGISTRY_PASSTHROUGH, Entry::data,
			Entry::new
		);

	}

	public static final class Synchronizer<T> {

		private static final BiConsumer<RegistryFriendlyByteBuf, Map<ResourceLocation, Collection<PowerReference>>> MAP_ENCODER = (buf, map) -> buf.writeMap(map,
			FriendlyByteBuf::writeResourceLocation,
			(valueBuf, references) -> valueBuf.writeCollection(references, PowerReference.STREAM_CODEC)
		);

		private static final Function<RegistryFriendlyByteBuf, Map<ResourceLocation, Collection<PowerReference>>> MAP_DECODER = buf -> buf.readMap(
			FriendlyByteBuf::readResourceLocation,
			valueBuf -> valueBuf.readCollection(ObjectArrayList::new, PowerReference.STREAM_CODEC)
		);

		public static final Synchronizer<Map<ResourceLocation, Collection<PowerReference>>> GRANT = new Synchronizer<>(
			GRANT_SYNC_ID,
			MAP_ENCODER,
			MAP_DECODER,
			(powersComponent, map) -> map.forEach((source, ids) ->
				ids.forEach(id -> powersComponent.grantPowerSideAgnostic(id, source))
			)
		);

		public static final Synchronizer<Map<ResourceLocation, Collection<PowerReference>>> REVOKE = new Synchronizer<>(
			REVOKE_SYNC_ID,
			MAP_ENCODER,
			MAP_DECODER,
			(powersComponent, map) -> map.forEach((source, ids) ->
				ids.forEach(id -> powersComponent.revokePowerSideAgnostic(id, source))
			)
		);

		private final int id;
		private final BiConsumer<RegistryFriendlyByteBuf, T> encoder;
		private final Function<RegistryFriendlyByteBuf, T> decoder;
		private final BiConsumer<PowersComponent, T> processor;

		private Synchronizer(int id, BiConsumer<RegistryFriendlyByteBuf, T> encoder, Function<RegistryFriendlyByteBuf, T> decoder, BiConsumer<PowersComponent, T> processor) {
			this.id = id;
			this.encoder = encoder;
			this.decoder = decoder;
			this.processor = processor;
		}

		public void sync(Entity holder, T t) {
			NeoApoliEntityComponents.POWERS.sync(holder, (buf, recipient) -> this.send(buf, t));
		}

		public void send(RegistryFriendlyByteBuf buf, T t) {
			buf.writeVarInt(id);
			encoder.accept(buf, t);
		}

		public void receive(RegistryFriendlyByteBuf buf, PowersComponent powersComponent) {
			processor.accept(powersComponent, decoder.apply(buf));
		}

	}

}
