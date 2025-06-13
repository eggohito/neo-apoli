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
import io.github.eggohito.neo_apoli.power.PowerSerializers;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
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
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public final class PowersComponent implements Component, AutoSyncedComponent, CommonTickingComponent, RespawnableComponent<PowersComponent> {

	private static final int FULL_SYNC_ID = 0;
	private static final int GRANT_SYNC_ID = 1;
	private static final int REVOKE_SYNC_ID = 2;

	private final Map<PowerReference, Power.Type<?>> types;
	private final Map<PowerReference, Set<Identifier>> sources;

	private final Entity holder;

	public PowersComponent(Entity holder) {
		this.types = new ConcurrentHashMap<>();
		this.sources = new ConcurrentHashMap<>();
		this.holder = holder;
	}

	@Override
	public void writeToNbt(NbtCompound rootNbt, RegistryWrapper.WrapperLookup wrapperLookup) {

		RegistryOps<NbtElement> nbtOps = wrapperLookup.getOps(NbtOps.INSTANCE);
		NbtList powersNbt = new NbtList();

		this.types.forEach((id, impl) -> {

			Set<Identifier> sources = this.sources.getOrDefault(id, Set.of());
			NbtElement data = impl.encodeData(nbtOps)
				.mapError(error -> "Error trying to encode data of " + id.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (defaulting to empty NBT): " + error)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.orElseGet(nbtOps::emptyMap);

			Entry<NbtElement> entry = new Entry<>(id, impl.getPower().getSerializer(), sources, new Dynamic<>(nbtOps, data));
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

		this.types.clear();
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

						Power.Type<?> type = power.createType(holder);
						Set<Identifier> sources = entry.sources();

						if (Objects.equals(entry.serializer(), power.getSerializer())) {
							type.decodeData(nbtOps, data.getValue())
								.mapError(error -> "Error decoding data of " + powerReference.asDisplayString(false) + " from NBT (skipping): " + error)
								.error()
								.map(DataResult.Error::message)
								.ifPresent(NeoApoli.LOGGER::warn);
						}

						else {
							NeoApoli.LOGGER.warn("Power type of {} has changed. Its data won't be recovered!", powerReference.asDisplayString(false));
						}

						this.types.put(powerReference, type);
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

		for (var impl : types.values()) {

			if (impl.shouldTick()) {
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

		List<Power.Type<?>> addedPowers = new ObjectArrayList<>();
		List<Power.Type<?>> grantedPowers = new ObjectArrayList<>();

		boolean granted = this.grantPower(entry, source, addedPowers::add, grantedPowers::add);

		addedPowers.forEach(Power.Type::onAdded);
		grantedPowers.forEach(Power.Type::onGranted);

		return granted;

	}

	private boolean grantPower(PowerEntry<?> entry, Identifier source, Consumer<Power.Type<?>> addedAction, Consumer<Power.Type<?>> grantedAction) {

		PowerReference reference = entry.reference();
		Set<Identifier> sources = this.sources.computeIfAbsent(reference, k -> new ObjectOpenHashSet<>());

		if (sources.contains(source)) {
			return false;
		}

		Power power = entry.value();
		Power.Type<?> type = power.createType(holder);

		sources.add(source);
		addedAction.accept(type);

		if (!types.containsKey(reference)) {
			grantedAction.accept(type);
		}

		this.types.put(reference, type);
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

		types.keySet().removeIf(revokedPowers::contains);
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

		if (types.containsKey(reference)) {

			Power.Type<?> type = types.get(reference);
			type.onRemoved();

			if (sources.isEmpty()) {
				type.onRevoked();
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

	public void forEach(TriConsumer<PowerReference, Power.Type<?>, Set<Identifier>> consumer) {

		for (var implEntry : this.types.entrySet()) {

			PowerReference reference = implEntry.getKey();
			Set<Identifier> sources = this.sources.getOrDefault(reference, new ObjectOpenHashSet<>());

			if (!sources.isEmpty()) {
				consumer.accept(reference, implEntry.getValue(), sources);
			}

		}

	}

	public List<Power.Type<?>> getPowerTypes() {

		List<Power.Type<?>> collected = new ObjectArrayList<>();
		this.forEach((reference, type, sources) -> collected.add(type));

		return collected;

	}

	public <T extends Power.Type<?>> List<T> getPowerTypes(Class<T> typeClass) {
		return this.getPowerTypes(typeClass, type -> true);
	}

	public <T extends Power.Type<?>> List<T> getPowerTypes(Class<T> typeClass, Predicate<T> filter) {

		List<T> collected = new ObjectArrayList<>();
		this.forEach((reference, type, sources) -> {

			if (typeClass.isInstance(type)) {

				T castedType = typeClass.cast(type);

				if (filter.test(castedType)) {
					collected.add(castedType);
				}

			}

		});

		return collected;

	}

	public List<Power> getPowers(boolean includeSubPowers) {

		List<Power> collected = new ObjectArrayList<>();
		this.forEach((reference, type, sources) -> {

			if (includeSubPowers || !reference.isSubPower()) {
				collected.add(type.getPower());
			}

		});

		return collected;

	}

	public List<Power> getPowersFromSource(Identifier source) {

		List<Power> collected = new ObjectArrayList<>();
		this.forEach((reference, impl, sources) -> {

			if (sources.contains(source)) {
				collected.add(impl.getPower());
			}

		});

		return collected;

	}

	public Power.Type<?> getPowerType(PowerReference reference) {
		return Objects.requireNonNull(types.get(reference), "Entity " + holder.getName().getString() + " didn't have " + reference.asDisplayString(false) + " granted!");
	}

	public <T extends Power.Type<?>> boolean hasPowerType(Class<T> typeClass) {
		return this.hasPowerType(typeClass, type -> true);
	}

	public <T extends Power.Type<?>> boolean hasPowerType(Class<T> typeClass, Predicate<T> filter) {

		for (var type : types.values()) {

			if (typeClass.isInstance(type) && filter.test(typeClass.cast(type))) {
				return true;
			}

		}

		return false;

	}

	public boolean hasPower(PowerReference reference) {
		return types.containsKey(reference)
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
		this.forEach((reference, impl, sources) -> {

			if (sources.contains(source)) {
				collected.add(reference);
			}

		});

		return collected;

	}

	public static <I extends Power.Type<?>> boolean hasPowerType(@NotNull Entity entity, Class<I> typeClass) {
		return NeoApoliEntityComponents.POWERS.get(entity).hasPowerType(typeClass);
	}

	public static <T extends Power.Type<?>> boolean hasPowerType(@NotNull Entity entity, Class<T> typeClass, Predicate<T> filter) {
		return NeoApoliEntityComponents.POWERS.get(entity).hasPowerType(typeClass, filter);
	}

	public static List<Power.Type<?>> getPowerTypes(@NotNull Entity entity) {
		return NeoApoliEntityComponents.POWERS.get(entity).getPowerTypes();
	}

	public static <T extends Power.Type<?>> List<T> getPowerTypes(@NotNull Entity entity, Class<T> typeClass) {
		return NeoApoliEntityComponents.POWERS.get(entity).getPowerTypes(typeClass);
	}

	public static <I extends Power.Type<?>> List<I> getPowerTypes(@NotNull Entity entity, Class<I> typeClass, Predicate<I> typeFilter) {
		return NeoApoliEntityComponents.POWERS.get(entity).getPowerTypes(typeClass, typeFilter);
	}

	public record Entry<T>(PowerReference powerReference, Power.Serializer<?> serializer, Set<Identifier> sources, Dynamic<T> data) {

		public static final MapCodec<Entry<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PowerReference.CODEC.fieldOf("id").forGetter(Entry::powerReference),
			PowerSerializers.CODEC.fieldOf("type").forGetter(Entry::serializer),
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
