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
import java.util.function.*;

@SuppressWarnings("UnstableApiUsage")
public final class PowersComponent implements Component, AutoSyncedComponent, CommonTickingComponent, RespawnableComponent<PowersComponent> {

	private static final int FULL_SYNC_ID = 0;
	private static final int GRANT_SYNC_ID = 1;
	private static final int REVOKE_SYNC_ID = 2;

	private final Map<PowerReference, Power.Impl<?>> impls;
	private final Map<PowerReference, Set<Identifier>> sources;

	private final Entity holder;

	public PowersComponent(Entity holder) {
		this.impls = new ConcurrentHashMap<>();
		this.sources = new ConcurrentHashMap<>();
		this.holder = holder;
	}

	@Override
	public void writeToNbt(NbtCompound rootNbt, RegistryWrapper.WrapperLookup wrapperLookup) {

		RegistryOps<NbtElement> nbtOps = wrapperLookup.getOps(NbtOps.INSTANCE);
		NbtList powersNbt = new NbtList();

		this.impls.forEach((id, impl) -> {

			Set<Identifier> sources = this.sources.getOrDefault(id, Set.of());
			NbtElement data = impl.encodeData(nbtOps)
				.mapError(error -> "Error trying to encode data of " + id.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (defaulting to empty NBT): " + error)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.orElseGet(nbtOps::emptyMap);

			Entry<NbtElement> entry = new Entry<>(id, impl.getPower().getType(), sources, new Dynamic<>(nbtOps, data));
			Entry.CODEC.encoder().encodeStart(nbtOps, entry)
				.mapError(error -> "Error trying to encode " + id.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (skipping): " + error)
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

		this.impls.clear();
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

						Power.Impl<?> impl = power.createImpl(holder);
						Set<Identifier> sources = entry.sources();

						if (Objects.equals(entry.type(), power.getType())) {
							impl.decodeData(nbtOps, data.getValue())
								.mapError(error -> "Error decoding data of " + powerReference.asDisplayString(false) + " from NBT (skipping): " + error)
								.error()
								.map(DataResult.Error::message)
								.ifPresent(NeoApoli.LOGGER::warn);
						}

						else {
							NeoApoli.LOGGER.warn("Power type of {} has changed. Its data won't be recovered!", powerReference.asDisplayString(false));
						}

						this.impls.put(powerReference, impl);
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

		for (var impl : impls.values()) {

			if (impl.ticking() && (impl.tickingWhenInactive() || impl.isActive())) {
				impl.onTick();
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

		List<Power.Impl<?>> addedPowers = new ObjectArrayList<>();
		List<Power.Impl<?>> grantedPowers = new ObjectArrayList<>();

		boolean granted = this.grantPower(entry, source, addedPowers::add, grantedPowers::add);

		addedPowers.forEach(Power.Impl::onAdded);
		grantedPowers.forEach(Power.Impl::onGranted);

		return granted;

	}

	private boolean grantPower(PowerEntry<?> entry, Identifier source, Consumer<Power.Impl<?>> addedAction, Consumer<Power.Impl<?>> grantedAction) {

		PowerReference reference = entry.reference();
		Set<Identifier> sources = this.sources.computeIfAbsent(reference, k -> new ObjectOpenHashSet<>());

		if (sources.contains(source)) {
			return false;
		}

		Power power = entry.value();
		Power.Impl<?> impl = power.createImpl(holder);

		sources.add(source);
		addedAction.accept(impl);

		if (!impls.containsKey(reference)) {
			grantedAction.accept(impl);
		}

		this.impls.put(reference, impl);
		if (power instanceof MultiplePower multiplePower) {

			for (Power subPower : multiplePower.getSubPowers().values()) {

				if (!PowerManager.containsReference(subPower)) {
					continue;
				}

				PowerReference subReference = PowerManager.getReference(subPower);
				PowerEntry<?> subEntry = PowerManager.getEntry(subReference);

				grantPower(subEntry, source, addedAction, grantedAction);

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

		impls.keySet().removeIf(revokedPowers::contains);
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

		if (impls.containsKey(reference)) {

			Power.Impl<?> impl = impls.get(reference);
			impl.onRemoved();

			if (sources.isEmpty()) {
				impl.onRevoked();
				revokedAction.accept(reference);
			}

		}

		if (power instanceof MultiplePower multiplePower) {

			for (Power subPower : multiplePower.getSubPowers().values()) {

				if (!PowerManager.containsReference(subPower)) {
					continue;
				}

				PowerReference subReference = PowerManager.getReference(subPower);
				PowerEntry<?> subEntry = PowerManager.getEntry(subReference);

				revokePower(subEntry, source, revokedAction);

			}

		}

		return true;

	}


	public <T, C extends Collection<T>> C collectAndMap(Supplier<C> collectionConstructor, BiFunction<PowerReference, Power.Impl<?>, T> mapper, boolean includeSubPowers) {

		C collected = collectionConstructor.get();
		for (var implEntry : this.impls.entrySet()) {

			PowerReference reference = implEntry.getKey();
			Power.Impl<?> impl = implEntry.getValue();

			if (includeSubPowers || !reference.isSubPower()) {
				collected.add(mapper.apply(reference, impl));
			}

		}

		return collected;

	}

	public <T, C extends Collection<T>> C collectAndMapFromSource(Supplier<C> collectionConstructor, BiFunction<PowerReference, Power.Impl<?>, T> mapper, Identifier source) {

		C collected = collectionConstructor.get();
		for (var sourceEntry : this.sources.entrySet()) {

			PowerReference reference = sourceEntry.getKey();
			Set<Identifier> sources = sourceEntry.getValue();

			if (sources.contains(source) && this.impls.containsKey(reference)) {
				collected.add(mapper.apply(reference, this.impls.get(reference)));
			}

		}

		return collected;

	}

	public Set<Power.Impl<?>> getPowers(boolean includeSubPowers) {
		return collectAndMap(ObjectOpenHashSet::new, (reference, impl) -> impl, includeSubPowers);
	}

	public Set<Power.Impl<?>> getPowersFromSource(Identifier source) {
		return collectAndMapFromSource(ObjectOpenHashSet::new, (reference, impl) -> impl, source);
	}

	public Power.Impl<?> getPower(PowerReference reference) {
		return Objects.requireNonNull(impls.get(reference), "Entity " + holder.getName().getString() + " didn't have " + reference.asDisplayString(false) + " granted!");
	}

	public Set<Identifier> getSources(PowerReference reference) {
		return sources.containsKey(reference)
			? new ObjectOpenHashSet<>(sources.get(reference))
			: ObjectOpenHashSet.of();
	}

	public boolean hasPower(PowerReference reference) {
		return impls.containsKey(reference)
			&& sources.containsKey(reference);
	}

	public boolean hasPower(PowerReference reference, Identifier source) {
		return hasPower(reference)
			&& sources.get(reference).contains(source);
	}

	public record Entry<T>(PowerReference powerReference, PowerType<?> type, Set<Identifier> sources, Dynamic<T> data) {

		public static final MapCodec<Entry<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PowerReference.CODEC.fieldOf("id").forGetter(Entry::powerReference),
			PowerTypes.CODEC.fieldOf("type").forGetter(Entry::type),
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
