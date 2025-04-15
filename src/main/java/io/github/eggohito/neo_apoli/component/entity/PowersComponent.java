package io.github.eggohito.neo_apoli.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.internal.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.PowerEntry;
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
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public final class PowersComponent implements Component, AutoSyncedComponent, CommonTickingComponent, RespawnableComponent<PowersComponent> {

	private static final int FULL_SYNC_ID = 0;
	private static final int GRANT_SYNC_ID = 1;
	private static final int REVOKE_SYNC_ID = 2;

	private final Map<PowerReference, PowerEntry<?>> powers;
	private final Map<PowerReference, Set<Identifier>> sources;

	private final Entity holder;

	public PowersComponent(Entity holder) {
		this.powers = new ConcurrentHashMap<>();
		this.sources = new ConcurrentHashMap<>();
		this.holder = holder;
	}

	@Override
	public void writeToNbt(NbtCompound rootNbt, RegistryWrapper.WrapperLookup wrapperLookup) {

		RegistryOps<NbtElement> nbtOps = wrapperLookup.getOps(NbtOps.INSTANCE);
		NbtList powersNbt = new NbtList();

		this.powers.forEach((id, powerEntry) -> {

			Set<Identifier> sources = this.sources.getOrDefault(id, Collections.emptySet());
			Power power = powerEntry.value();

			NbtElement data = power.encodeData(nbtOps)
				.mapError(err -> "Error trying to encode data of " + id.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (defaulting to empty NBT): " + err)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.orElseGet(NbtCompound::new);

			Entry<NbtElement> entry = new Entry<>(id, power.getType(), sources, new Dynamic<>(nbtOps, data));
			Entry.CODEC.encoder().encodeStart(nbtOps, entry)
				.mapError(err -> "Error trying to encode " + id.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (skipping): " + err)
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

		this.powers.clear();
		this.sources.clear();

		while (powersNbtIterator.hasNext()) {

			NbtElement powerNbt = powersNbtIterator.next();
			int index = powersNbtIterator.nextIndex();

			try {

				Entry<?> entry = Entry.CODEC.decoder()
					.parse(nbtOps, powerNbt)
					.getOrThrow();

				PowerReference powerReference = entry.powerReference();
				DataResult<Power> powerResult = PowerManager.getAsResult(powerReference).flatMap(this::deepCopy);

				switch (powerResult) {
					case DataResult.Success<Power> success -> {

						Power power = success.value();
						Dynamic<NbtElement> data = entry.data().convert(nbtOps);

						PowerEntry<?> powerEntry = new PowerEntry<>(powerReference, power);
						Set<Identifier> sources = entry.sources();

						if (Objects.equals(entry.powerType(), power.getType())) {
							power.decodeData(nbtOps, data.getValue())
								.mapError(error -> "Error decoding data of " + powerReference.asDisplayString(false) + " from NBT (skipping): " + error)
								.error()
								.map(DataResult.Error::message)
								.ifPresent(NeoApoli.LOGGER::warn);
						}

						else {
							NeoApoli.LOGGER.warn("Power type of {} has changed. Its data won't be recovered!", powerReference.asDisplayString(false));
						}

						this.powers.put(powerReference, powerEntry);
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

		switch (buf.readVarInt()) {
			case FULL_SYNC_ID ->
				AutoSyncedComponent.super.applySyncPacket(buf);
			case GRANT_SYNC_ID ->
				Synchronizer.GRANT.receive(buf, this);
			case REVOKE_SYNC_ID ->
				Synchronizer.REVOKE.receive(buf, this);
			default ->
				NeoApoli.LOGGER.warn("Entity {} (UUID: {}) received powers component sync packet with unknown sync ID!", holder.getName().getString(), holder.getUuidAsString());
		}

	}

	@Override
	public void tick() {

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

		List<Power> addedPowers = new ObjectArrayList<>();
		List<Power> grantedPowers = new ObjectArrayList<>();

		boolean granted = this.grantPower(entry, source, addedPowers::add, grantedPowers::add);

		addedPowers.forEach(addedPower -> addedPower.onAdded(holder));
		grantedPowers.forEach(grantedPower -> grantedPower.onGranted(holder));

		return granted;

	}

	private boolean grantPower(PowerEntry<?> entry, Identifier source, Consumer<Power> addedAction, Consumer<Power> grantedAction) {

		PowerReference reference = entry.reference();
		Set<Identifier> sources = this.sources.computeIfAbsent(reference, k -> new ObjectOpenHashSet<>());

		if (sources.contains(source)) {
			return false;
		}

		Power originalPower = entry.value();
		Power copiedPower = deepCopy(originalPower).getOrThrow();

		sources.add(source);
		addedAction.accept(copiedPower);

		if (!powers.containsKey(reference)) {
			grantedAction.accept(copiedPower);
		}

		this.powers.put(reference, new PowerEntry<>(reference, copiedPower));

		if (originalPower instanceof MultiplePower multiplePower) {
			multiplePower.getSubPowers().values()
				.stream()
				.map(PowerManager::getReference)
				.map(PowerManager::getEntry)
				.forEach(subEntry -> grantPower(subEntry, source, addedAction, grantedAction));
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

		powers.keySet().removeIf(revokedPowers::contains);
		sources.keySet().removeIf(revokedPowers::contains);

		return result;

	}

	private boolean revokePower(PowerEntry<?> entry, Identifier source, Consumer<PowerReference> revokedAction) {

		PowerReference reference = entry.reference();
		Set<Identifier> sources = this.sources.getOrDefault(reference, new ObjectOpenHashSet<>());

		if (!sources.contains(source)) {
			return false;
		}

		Power originalPower = entry.value();
		sources.remove(source);

		if (powers.containsKey(reference)) {

			Power storedPower = powers.get(reference).value();
			storedPower.onRemoved(holder);

			if (sources.isEmpty()) {
				storedPower.onRevoked(holder);
				revokedAction.accept(reference);
			}

		}

		if (originalPower instanceof MultiplePower multiplePower) {
			multiplePower.getSubPowers().values()
				.stream()
				.map(PowerManager::getReference)
				.map(PowerManager::getEntry)
				.forEach(subEntry -> revokePower(subEntry, source, revokedAction));
		}

		return true;

	}


	public <T, C extends Collection<T>> C collectAndMapPowerEntries(Supplier<C> collectionConstructor, Function<PowerEntry<?>, T> mapper, boolean includeSubPowers) {

		C collected = collectionConstructor.get();
		for (PowerEntry<?> entry : this.powers.values()) {

			if (includeSubPowers || !entry.isSubPower()) {
				collected.add(mapper.apply(entry));
			}

		}

		return collected;

	}

	public <T, C extends Collection<T>> C collectAndMapPowerEntriesFromSource(Supplier<C> collectionConstructor, Function<PowerEntry<?>, T> mapper, Identifier source) {

		C collected = collectionConstructor.get();
		for (var sourceEntry : this.sources.entrySet()) {

			PowerReference reference = sourceEntry.getKey();
			Set<Identifier> sources = sourceEntry.getValue();

			if (sources.contains(source) && this.powers.containsKey(reference)) {
				collected.add(mapper.apply(this.powers.get(reference)));
			}

		}

		return collected;

	}

	public Set<PowerEntry<?>> getPowerEntries(boolean includeSubPowers) {
		return collectAndMapPowerEntries(ObjectOpenHashSet::new, Function.identity(), includeSubPowers);
	}

	public Set<Power> getPowers(boolean includeSubPowers) {
		return collectAndMapPowerEntries(ObjectOpenHashSet::new, PowerEntry::value, includeSubPowers);
	}

	public PowerEntry<?> getPowerEntry(PowerReference reference) {
		return Objects.requireNonNull(powers.get(reference), "Entity " + holder.getName().getString() + " didn't have " + reference.asDisplayString(false) + " granted!");
	}

	public Power getPower(PowerReference reference) {
		return getPowerEntry(reference).value();
	}

	public Set<PowerEntry<?>> getPowerEntriesFromSource(Identifier source) {
		return collectAndMapPowerEntriesFromSource(ObjectOpenHashSet::new, Function.identity(), source);
	}

	public Set<Power> getPowersFromSource(Identifier source) {
		return collectAndMapPowerEntriesFromSource(ObjectOpenHashSet::new, PowerEntry::value, source);
	}

	public Set<Identifier> getSources(PowerReference reference) {
		return sources.containsKey(reference)
			? new ObjectOpenHashSet<>(sources.get(reference))
			: ObjectOpenHashSet.of();
	}

	public boolean hasPower(PowerReference reference) {
		return powers.containsKey(reference)
			&& sources.containsKey(reference);
	}

	public boolean hasPower(PowerReference reference, Identifier source) {
		return hasPower(reference)
			&& sources.get(reference).contains(source);
	}

	private DataResult<Power> deepCopy(Power power) {
		RegistryOps<NbtElement> nbtOps = holder.getRegistryManager().getOps(NbtOps.INSTANCE);
		return Power.CODEC.encodeStart(nbtOps, power).flatMap(nbtElement -> Power.CODEC.parse(nbtOps, nbtElement));
	}

	public record Entry<T>(PowerReference powerReference, PowerType<?> powerType, Set<Identifier> sources, Dynamic<T> data) {

		public static final MapCodec<Entry<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PowerReference.CODEC.fieldOf("id").forGetter(Entry::powerReference),
			PowerTypes.CODEC.fieldOf("type").forGetter(Entry::powerType),
			NeoApoliCodecs.MUTABLE_NON_EMPTY_IDENTIFIER_SET.fieldOf("sources").forGetter(Entry::sources),
			Codec.PASSTHROUGH.fieldOf("data").forGetter(Entry::data)
		).apply(instance, Entry::new));

	}

	public static final class Synchronizer<T> {

		public static final Synchronizer<Map<Identifier, Collection<PowerReference>>> GRANT = new Synchronizer<>(
			GRANT_SYNC_ID,
			(buf, map) -> buf.writeMap(
				map,
				PacketByteBuf::writeIdentifier,
				(valueBuf, ids) -> valueBuf.writeCollection(ids, PowerReference.PACKET_CODEC)
			),
			buf -> buf.readMap(
				PacketByteBuf::readIdentifier,
				valueBuf -> valueBuf.readCollection(ObjectArrayList::new, PowerReference.PACKET_CODEC)
			),
			(powersComponent, map) -> map.forEach((source, ids) ->
				ids.forEach(id -> powersComponent.grantPowerSideAgnostic(id, source))
			)
		);

		public static final Synchronizer<Map<Identifier, Collection<PowerReference>>> REVOKE = new Synchronizer<>(
			REVOKE_SYNC_ID,
			GRANT.encoder,
			GRANT.decoder,
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
