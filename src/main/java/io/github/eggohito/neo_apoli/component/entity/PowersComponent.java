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
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.PowerEntry;
import io.github.eggohito.neo_apoli.util.PowerIdentifier;
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
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public final class PowersComponent implements Component, AutoSyncedComponent, ClientTickingComponent, ServerTickingComponent {

	private static final int FULL_SYNC_ID = 0;
	private static final int GRANT_SYNC_ID = 1;
	private static final int REVOKE_SYNC_ID = 2;

	private final Map<PowerIdentifier, PowerEntry<?>> powers;
	private final Map<PowerIdentifier, Set<Identifier>> sources;

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

		NbtList powersNbt = rootNbt.getList("powers", NbtElement.COMPOUND_TYPE);
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

				PowerIdentifier powerId = entry.powerId();
				DataResult<Power> powerResult = PowerManager.getAsResult(powerId).flatMap(this::deepCopy);

				switch (powerResult) {
					case DataResult.Success<Power> success -> {

						Power power = success.value();
						Dynamic<NbtElement> data = entry.data().convert(nbtOps);

						PowerEntry<?> powerEntry = new PowerEntry<>(powerId, power);
						Set<Identifier> sources = entry.sources();

						try {

							if (Objects.equals(entry.powerType(), power.getType())) {
								power.decodeData(nbtOps, data.getValue());
							}

							else {
								NeoApoli.LOGGER.warn("Power type of {} has changed. Its data won't be recovered and will be skipped.", powerId.asDisplayString(false));
							}

						}

						catch (Exception e) {
							NeoApoli.LOGGER.warn("There was a problem decoding data of {} from NBT (skipping): {}", powerId.asDisplayString(false), e);
						}

						this.powers.put(powerId, powerEntry);
						this.sources.put(powerId, sources);

					}
					case DataResult.Error<Power> error ->
						NeoApoli.LOGGER.warn("Error decoding {} from cardinal_components.\"{}\".powers[{}] of entity {} (skipping): {}", powerId.asDisplayString(false), NeoApoliEntityComponents.POWERS.getId(), index, holder.getName().getString(), error.message());
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
	public void clientTick() {

	}

	@Override
	public void serverTick() {

	}

	public boolean grantPower(PowerIdentifier id, Identifier source) {
		return !holder.getWorld().isClient()
			&& grantPowerSideAgnostic(id, source);
	}

	private boolean grantPowerSideAgnostic(PowerIdentifier id, Identifier source) {
		return PowerManager.getEntryAsResult(id)
			.mapError(error -> "Error trying to grant " + id.asDisplayString(false) + " from source '" + source + "' to entity " + (holder.getName().getString() + " (UUID: " + holder.getUuidAsString() + ")") + " (skipping): " + error)
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

		PowerIdentifier id = entry.id();
		Set<Identifier> sources = this.sources.computeIfAbsent(id, k -> new ObjectOpenHashSet<>());

		if (sources.contains(source)) {
			return false;
		}

		Power originalPower = entry.value();
		Power copiedPower = deepCopy(originalPower).getOrThrow();

		sources.add(source);
		addedAction.accept(copiedPower);

		if (!powers.containsKey(id)) {
			grantedAction.accept(copiedPower);
		}

		this.powers.put(id, new PowerEntry<>(id, copiedPower));

		if (originalPower instanceof MultiplePower multiplePower) {
			multiplePower.getSubPowers().values()
				.stream()
				.map(PowerManager::getId)
				.map(PowerManager::getEntry)
				.forEach(subEntry -> grantPower(subEntry, source, addedAction, grantedAction));
		}

		return true;

	}

	public boolean revokePower(PowerIdentifier id, Identifier source) {
		return !holder.getWorld().isClient()
			&& revokePowerSideAgnostic(id, source);
	}

	private boolean revokePowerSideAgnostic(PowerIdentifier id, Identifier source) {
		return PowerManager.getEntryAsResult(id)
			.mapError(error -> "Error trying to revoke " + id.asDisplayString(false) + " from source '" + source + "' to entity " + (holder.getName().getString() + " (UUID: " + holder.getUuidAsString() + ")") + " (skipping): " + error)
			.resultOrPartial(NeoApoli.LOGGER::warn)
			.map(entry -> revokePower(entry, source))
			.orElse(false);
	}

	private boolean revokePower(PowerEntry<?> entry, Identifier source) {

		List<PowerIdentifier> revokedPowerIds = new ObjectArrayList<>();
		boolean result = this.revokePower(entry, source, revokedPowerIds::add);

		powers.keySet().removeIf(revokedPowerIds::contains);
		sources.keySet().removeIf(revokedPowerIds::contains);

		return result;

	}

	private boolean revokePower(PowerEntry<?> entry, Identifier source, Consumer<PowerIdentifier> revokedAction) {

		PowerIdentifier id = entry.id();
		Set<Identifier> sources = this.sources.getOrDefault(id, new ObjectOpenHashSet<>());

		if (!sources.contains(source)) {
			return false;
		}

		Power originalPower = entry.value();
		sources.remove(source);

		if (powers.containsKey(id)) {

			Power storedPower = powers.get(id).value();
			storedPower.onRemoved(holder);

			if (sources.isEmpty()) {
				storedPower.onRevoked(holder);
				revokedAction.accept(id);
			}

		}

		if (originalPower instanceof MultiplePower multiplePower) {
			multiplePower.getSubPowers().values()
				.stream()
				.map(PowerManager::getId)
				.map(PowerManager::getEntry)
				.forEach(subEntry -> revokePower(subEntry, source, revokedAction));
		}

		return true;

	}

	public Stream<PowerEntry<?>> streamPowerEntries(boolean includeSubPowers) {
		return powers.values()
			.stream()
			.filter(entry -> includeSubPowers || !entry.isSubPower());
	}

	public Stream<Power> streamPowers(boolean includeSubPowers) {
		return streamPowerEntries(includeSubPowers).map(PowerEntry::value);
	}

	public PowerEntry<?> getPowerEntry(PowerIdentifier id) {
		return Objects.requireNonNull(powers.get(id), "Entity " + holder.getName().getString() + " didn't have " + id.asDisplayString(false) + " granted!");
	}

	public Power getPower(PowerIdentifier id) {
		return getPowerEntry(id).value();
	}

	public Set<Identifier> getSources(PowerIdentifier id) {
		return Objects.requireNonNull(sources.get(id), "Entity " + holder.getName().getString() + " didn't have any sources for " + id.asDisplayString(false) + "!");
	}

	public boolean hasPower(PowerIdentifier id) {
		return powers.containsKey(id)
			&& sources.containsKey(id);
	}

	public boolean hasPower(PowerIdentifier id, Identifier source) {
		return hasPower(id)
			&& sources.get(id).contains(source);
	}

	private DataResult<Power> deepCopy(Power power) {
		RegistryOps<NbtElement> nbtOps = holder.getRegistryManager().getOps(NbtOps.INSTANCE);
		return Power.BASE_CODEC.encodeStart(nbtOps, power).flatMap(nbtElement -> Power.BASE_CODEC.parse(nbtOps, nbtElement));
	}

	public record Entry<T>(PowerIdentifier powerId, PowerType<?> powerType, Set<Identifier> sources, Dynamic<T> data) {

		public static final MapCodec<Entry<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PowerIdentifier.CODEC.fieldOf("id").forGetter(Entry::powerId),
			NeoApoliRegistries.POWER_TYPE.getCodec().fieldOf("type").forGetter(Entry::powerType),
			NeoApoliCodecs.MUTABLE_NON_EMPTY_IDENTIFIER_SET.fieldOf("sources").forGetter(Entry::sources),
			Codec.PASSTHROUGH.fieldOf("data").forGetter(Entry::data)
		).apply(instance, Entry::new));

	}

	public static final class Synchronizer<T> {

		public static final Synchronizer<Map<Identifier, Collection<PowerIdentifier>>> GRANT = new Synchronizer<>(
			GRANT_SYNC_ID,
			(buf, map) -> buf.writeMap(
				map,
				PacketByteBuf::writeIdentifier,
				(valueBuf, ids) -> valueBuf.writeCollection(ids, PowerIdentifier.PACKET_CODEC)
			),
			buf -> buf.readMap(
				PacketByteBuf::readIdentifier,
				valueBuf -> valueBuf.readCollection(ObjectArrayList::new, PowerIdentifier.PACKET_CODEC)
			),
			(powersComponent, map) -> map.forEach((source, ids) ->
				ids.forEach(id -> powersComponent.grantPowerSideAgnostic(id, source))
			)
		);

		public static final Synchronizer<Map<Identifier, Collection<PowerIdentifier>>> REVOKE = new Synchronizer<>(
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
