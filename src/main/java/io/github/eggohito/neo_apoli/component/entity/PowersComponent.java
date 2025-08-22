package io.github.eggohito.neo_apoli.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.util.TriConsumer;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

@SuppressWarnings("UnstableApiUsage")
public final class PowersComponent implements Component, AutoSyncedComponent, CommonTickingComponent, RespawnableComponent<PowersComponent> {

	private static final int FULL_SYNC_ID = 0;
	private static final int GRANT_SYNC_ID = 1;
	private static final int REVOKE_SYNC_ID = 2;

	private final Map<PowerReference, Power.Instance<?>> instances;
	private final Map<PowerReference, Set<Identifier>> sources;

	private final Entity holder;

	public PowersComponent(Entity holder) {
		this.instances = new ConcurrentHashMap<>();
		this.sources = new ConcurrentHashMap<>();
		this.holder = holder;
	}

	@Override
	public void writeToNbt(NbtCompound rootNbt, RegistryWrapper.WrapperLookup wrapperLookup) {

		RegistryOps<NbtElement> nbtOps = wrapperLookup.getOps(NbtOps.INSTANCE);
		NbtList powersNbt = new NbtList();

		this.instances.forEach((powerReference, instance) -> {

			Set<Identifier> sources = this.sources.getOrDefault(powerReference, Set.of());
			NbtElement data = instance.encodeData(nbtOps)
				.mapError(error -> "Error trying to encode data of " + powerReference.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (defaulting to empty NBT): " + error)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.orElseGet(nbtOps::emptyMap);

			Entry<NbtElement> entry = new Entry<>(powerReference, instance.getPower().getType(), sources, new Dynamic<>(nbtOps, data));
			Entry.CODEC.encoder().encodeStart(nbtOps, entry)
				.mapError(error -> "Error trying to encode " + powerReference.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (skipping): " + error)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.ifPresent(powersNbt::add);

		});

		rootNbt.put("powers", powersNbt);

	}

	@Override
	public void readFromNbt(NbtCompound rootNbt, RegistryWrapper.WrapperLookup wrapperLookup) {

		RegistryOps<NbtElement> nbtOps = wrapperLookup.getOps(NbtOps.INSTANCE);

		NbtList powersNbt = rootNbt.getListOrEmpty("powers");
		ListIterator<NbtElement> powersNbtIterator = powersNbt.listIterator();

		this.instances.clear();
		this.sources.clear();

		while (powersNbtIterator.hasNext()) {

			int index = powersNbtIterator.nextIndex();
			NbtElement powerNbt = powersNbtIterator.next();

			try {

				Entry<?> entry = Entry.CODEC.decoder()
					.parse(nbtOps, powerNbt)
					.getOrThrow();

				PowerReference powerReference = entry.powerReference();
				DataResult<Power> powerResult = PowerManager.getAsResult(powerReference);

				switch (powerResult) {
					case DataResult.Success<Power> success -> {

						Power power = success.value();
						Dynamic<NbtElement> data = entry.data().convert(nbtOps);

						Power.Instance<?> instance = power.createInstance(holder);
						Set<Identifier> sources = entry.sources();

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
	public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
		buf.writeVarInt(FULL_SYNC_ID);
		AutoSyncedComponent.super.writeSyncPacket(buf, recipient);
	}

	@Override
	public void applySyncPacket(RegistryByteBuf buf) {

		int syncId = buf.readVarInt();

		switch (syncId) {
			case FULL_SYNC_ID ->
				AutoSyncedComponent.super.applySyncPacket(buf);
			case GRANT_SYNC_ID ->
				Synchronizer.GRANT.receive(buf, this);
			case REVOKE_SYNC_ID ->
				Synchronizer.REVOKE.receive(buf, this);
			default ->
				NeoApoli.LOGGER.warn("Entity {} (UUID: {}) received powers component sync packet with unknown sync ID (Expected 0-2, received {})!", holder.getName().getString(), holder.getUuidAsString(), syncId);
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

	public boolean grantPower(PowerReference reference, Identifier source) {
		return !holder.getWorld().isClient()
			&& grantPowerSideAgnostic(reference, source);
	}

	private boolean grantPowerSideAgnostic(PowerReference reference, Identifier source) {
		return PowerManager.getEntryAsResult(reference)
			.mapError(error -> "Error trying to grant " + reference.asDisplayString(false) + " from source '" + source + "' to entity " + (holder.getName().getString() + " (UUID: " + holder.getUuidAsString() + ")") + " (skipping): " + error)
			.resultOrPartial(NeoApoli.LOGGER::warn)
			.map(entry -> grantPower(entry, source))
			.orElse(false);
	}

	private boolean grantPower(PowerEntry<?> entry, Identifier source) {

		List<Power.Instance<?>> addedPowers = new ObjectArrayList<>();
		List<Power.Instance<?>> grantedPowers = new ObjectArrayList<>();

		boolean granted = this.grantPower(entry, source, addedPowers::add, grantedPowers::add);

		addedPowers.forEach(Power.Instance::onAdded);
		grantedPowers.forEach(Power.Instance::onGranted);

		return granted;

	}

	private boolean grantPower(PowerEntry<?> entry, Identifier source, Consumer<Power.Instance<?>> addedAction, Consumer<Power.Instance<?>> grantedAction) {

		PowerReference reference = entry.reference();
		Set<Identifier> sources = this.sources.computeIfAbsent(reference, k -> new ObjectOpenHashSet<>());

		if (sources.contains(source)) {
			return false;
		}

		Power power = entry.value();
		Power.Instance<?> instance = power.createInstance(holder);

		sources.add(source);
		addedAction.accept(instance);

		if (!instances.containsKey(reference)) {
			grantedAction.accept(instance);
		}

		this.instances.put(reference, instance);
		if (power instanceof MultiplePower multiplePower) {

			for (PowerReference.SubPower subReference : multiplePower.getSubPowers().keySet()) {
				PowerManager.getEntryAsResult(subReference).ifSuccess(subEntry -> this.grantPower(subEntry, source, addedAction, grantedAction));
			}

		}

		return true;

	}

	public boolean revokePower(PowerReference id, Identifier source) {
		return !holder.getWorld().isClient()
			&& revokePowerSideAgnostic(id, source);
	}

	private boolean revokePowerSideAgnostic(PowerReference reference, Identifier source) {
		return PowerManager.getEntryAsResult(reference)
			.mapError(error -> "Error trying to revoke " + reference.asDisplayString(false) + " from source '" + source + "' to entity " + (holder.getName().getString() + " (UUID: " + holder.getUuidAsString() + ")") + " (skipping): " + error)
			.resultOrPartial(NeoApoli.LOGGER::warn)
			.map(entry -> revokePower(entry, source))
			.orElse(false);
	}

	private boolean revokePower(PowerEntry<?> entry, Identifier source) {

		List<PowerReference> revokedPowers = new ObjectArrayList<>();
		boolean result = this.revokePower(entry, source, revokedPowers::add);

		instances.keySet().removeIf(revokedPowers::contains);
		sources.keySet().removeIf(revokedPowers::contains);

		return result;

	}

	private boolean revokePower(PowerEntry<?> entry, Identifier source, Consumer<PowerReference> revokedAction) {

		PowerReference reference = entry.reference();
		Set<Identifier> sources = this.sources.getOrDefault(reference, new ObjectOpenHashSet<>());

		if (!sources.contains(source)) {
			return false;
		}

		Power power = entry.value();
		sources.remove(source);

		if (instances.containsKey(reference)) {

			Power.Instance<?> instance = instances.get(reference);
			instance.onRemoved();

			if (sources.isEmpty()) {
				instance.onRevoked();
				revokedAction.accept(reference);
			}

		}

		if (power instanceof MultiplePower multiplePower) {

			for (PowerReference.SubPower subReference : multiplePower.getSubPowers().keySet()) {
				PowerManager.getEntryAsResult(subReference).ifSuccess(subEntry -> this.revokePower(subEntry, source, revokedAction));
			}

		}

		return true;

	}

	public void forEach(TriConsumer<PowerReference, Power.Instance<?>, Set<Identifier>> consumer, BooleanSupplier continueCondition) {

		for (var entry : this.instances.entrySet()) {

			PowerReference reference = entry.getKey();
			Set<Identifier> sources = this.sources.getOrDefault(reference, new ObjectOpenHashSet<>());

			if (!continueCondition.getAsBoolean()) {
				break;
			}

			else if (!sources.isEmpty()) {
				consumer.accept(reference, entry.getValue(), sources);
			}

		}

	}

	public void forEach(TriConsumer<PowerReference, Power.Instance<?>, Set<Identifier>> consumer) {
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

			if (includingSubPowers || !reference.isSubPower()) {
				collected.add(type.getPower());
			}

		});

		return collected;

	}

	public List<Power> getAllFromSource(Identifier source) {

		List<Power> collected = new ObjectArrayList<>();
		this.forEach((reference, instance, sources) -> {

			if (sources.contains(source)) {
				collected.add(instance.getPower());
			}

		});

		return collected;

	}

	public Power.Instance<?> getInstance(PowerReference reference) {
		return Objects.requireNonNull(instances.get(reference), "Entity " + holder.getName().getString() + " didn't have " + reference.asDisplayString(false) + " granted!");
	}

	public <I extends Power.Instance<?>> boolean hasInstance(Class<I> instanceClass) {
		return this.hasInstance(instanceClass, instance -> true);
	}

	public <I extends Power.Instance<?>> boolean hasInstance(Class<I> instanceClass, Predicate<I> instanceFilter) {

		MutableBoolean result = new MutableBoolean(false);
		TriConsumer<PowerReference, Power.Instance<?>, Set<Identifier>> consumer = (powerReference, instance, sources) -> {

			if (instanceClass.isInstance(instance) && instanceFilter.test(instanceClass.cast(instance))) {
				result.setTrue();
			}

		};

		this.forEach(consumer, result::isTrue);
		return result.isTrue();

	}

	public boolean hasPower(PowerReference reference) {
		return instances.containsKey(reference)
			&& sources.containsKey(reference);
	}

	public boolean hasPower(PowerReference reference, Identifier source) {
		return hasPower(reference)
			&& sources.get(reference).contains(source);
	}

	public Set<Identifier> getSources(PowerReference reference) {
		return sources.containsKey(reference)
			? new ObjectOpenHashSet<>(sources.get(reference))
			: ObjectOpenHashSet.of();
	}

	public Set<PowerReference> getReferences(Identifier source) {

		Set<PowerReference> collected = new ObjectOpenHashSet<>();
		this.forEach((reference, instance, sources) -> {

			if (sources.contains(source)) {
				collected.add(reference);
			}

		});

		return collected;

	}

	public static void forEach(Entity holder, TriConsumer<PowerReference, Power.Instance<?>, Set<Identifier>> consumer, BooleanSupplier continueCondition) {
		NeoApoliEntityComponents.POWERS.maybeGet(holder).ifPresent(powersComponent -> powersComponent.forEach(consumer, continueCondition));
	}

	public static void forEach(Entity holder, TriConsumer<PowerReference, Power.Instance<?>, Set<Identifier>> consumer) {
		NeoApoliEntityComponents.POWERS.maybeGet(holder).ifPresent(powersComponent -> powersComponent.forEach(consumer));
	}

	public static <I extends Power.Instance<?>> boolean hasInstance(Entity entity, Class<I> implClass) {
		return NeoApoliEntityComponents.POWERS.maybeGet(entity)
			.stream()
			.anyMatch(powersComponent -> powersComponent.hasInstance(implClass));
	}

	public static <I extends Power.Instance<?>> boolean hasInstance(Entity entity, Class<I> implClass, Predicate<I> implFilter) {
		return NeoApoliEntityComponents.POWERS.maybeGet(entity)
			.stream()
			.anyMatch(powersComponent -> powersComponent.hasInstance(implClass, implFilter));
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

	public record Entry<T>(PowerReference powerReference, PowerType<?> type, Set<Identifier> sources, Dynamic<T> data) {

		public static final MapCodec<Entry<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PowerReference.CODEC.fieldOf("id").forGetter(Entry::powerReference),
			PowerTypes.CODEC.fieldOf("type").forGetter(Entry::type),
			NeoApoliCodecs.MUTABLE_NON_EMPTY_IDENTIFIER_SET.fieldOf("sources").forGetter(Entry::sources),
			Codec.PASSTHROUGH.fieldOf("data").forGetter(Entry::data)
		).apply(instance, Entry::new));

		public static final PacketCodec<RegistryByteBuf, Entry<?>> PACKET_CODEC = PacketCodec.tuple(
			PowerReference.PACKET_CODEC, Entry::powerReference,
			PowerTypes.PACKET_CODEC, Entry::type,
			NeoApoliPacketCodecs.MUTABLE_NON_EMPTY_IDENTIFIER_SET, Entry::sources,
			NeoApoliPacketCodecs.REGISTRY_PASSTHROUGH, Entry::data,
			Entry::new
		);

	}

	public static final class Synchronizer<T> {

		private static final BiConsumer<RegistryByteBuf, Map<Identifier, Collection<PowerReference>>> MAP_ENCODER = (buf, map) -> buf.writeMap(map,
			PacketByteBuf::writeIdentifier,
			(valueBuf, references) -> valueBuf.writeCollection(references, PowerReference.PACKET_CODEC)
		);

		private static final Function<RegistryByteBuf, Map<Identifier, Collection<PowerReference>>> MAP_DECODER = buf -> buf.readMap(
			PacketByteBuf::readIdentifier,
			valueBuf -> valueBuf.readCollection(ObjectArrayList::new, PowerReference.PACKET_CODEC)
		);

		public static final Synchronizer<Map<Identifier, Collection<PowerReference>>> GRANT = new Synchronizer<>(
			GRANT_SYNC_ID,
			MAP_ENCODER,
			MAP_DECODER,
			(powersComponent, map) -> map.forEach((source, ids) ->
				ids.forEach(id -> powersComponent.grantPowerSideAgnostic(id, source))
			)
		);

		public static final Synchronizer<Map<Identifier, Collection<PowerReference>>> REVOKE = new Synchronizer<>(
			REVOKE_SYNC_ID,
			MAP_ENCODER,
			MAP_DECODER,
			(powersComponent, map) -> map.forEach((source, ids) ->
				ids.forEach(id -> powersComponent.revokePowerSideAgnostic(id, source))
			)
		);

		private final int id;
		private final BiConsumer<RegistryByteBuf, T> encoder;
		private final Function<RegistryByteBuf, T> decoder;
		private final BiConsumer<PowersComponent, T> processor;

		private Synchronizer(int id, BiConsumer<RegistryByteBuf, T> encoder, Function<RegistryByteBuf, T> decoder, BiConsumer<PowersComponent, T> processor) {
			this.id = id;
			this.encoder = encoder;
			this.decoder = decoder;
			this.processor = processor;
		}

		public void sync(Entity holder, T t) {
			NeoApoliEntityComponents.POWERS.sync(holder, (buf, recipient) -> this.send(buf, t));
		}

		public void send(RegistryByteBuf buf, T t) {
			buf.writeVarInt(id);
			encoder.accept(buf, t);
		}

		public void receive(RegistryByteBuf buf, PowersComponent powersComponent) {
			processor.accept(powersComponent, decoder.apply(buf));
		}

	}

}
