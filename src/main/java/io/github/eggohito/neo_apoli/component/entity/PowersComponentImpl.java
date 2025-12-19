package io.github.eggohito.neo_apoli.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class PowersComponentImpl implements PowersComponent {

	private static final byte GRANT_POWERS_UPDATE_ID = 0;
	private static final byte REVOKE_POWERS_UPDATE_ID = 1;

	private final Map<PowerEntry<?>, Power.Instance<?>> instances;
	private final Map<PowerEntry<?>, Set<ResourceLocation>> sources;

	private final Map<ResourceLocation, Set<PowerEntry<?>>> grantedPowers;
	private final Map<ResourceLocation, Set<PowerEntry<?>>> revokedPowers;

	private final Entity holder;

	public PowersComponentImpl(Entity holder) {
		this.instances = new ConcurrentHashMap<>();
		this.sources = new ConcurrentHashMap<>();
		this.grantedPowers = new Object2ObjectOpenHashMap<>();
		this.revokedPowers = new Object2ObjectOpenHashMap<>();
		this.holder = holder;
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
	public void readFromNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {

		RegistryOps<Tag> nbtOps = provider.createSerializationContext(NbtOps.INSTANCE);
		ListTag powersNbt = compoundTag.getListOrEmpty("powers");

		this.instances.clear();
		this.sources.clear();

		ListIterator<Tag> listIterator = powersNbt.listIterator();
		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			Tag powerNbt = listIterator.next();

			try {

				Data<?> data = Data.CODEC
					.parse(nbtOps, powerNbt)
					.getOrThrow();

				PowerReference reference = data.reference();
				DataResult<PowerEntry<?>> entryResult = PowerManager.getEntryAsResult(reference);

				switch (entryResult) {
					case DataResult.Success<PowerEntry<?>> success -> {

						Dynamic<Tag> encodedData = data.encoded().convert(nbtOps);
						Set<ResourceLocation> sources = data.sources();

						PowerEntry<?> entry = success.value();
						Power.Instance<?> instance = entry.power().createInstance(holder);

						if (Objects.equals(data.type(), entry.power().getType())) {
							instance.decodeData(nbtOps, encodedData.getValue())
								.mapError(error -> "Error decoding data of " + reference.asDisplayString(false) + " from NBT (skipping): " + error)
								.error()
								.map(DataResult.Error::message)
								.ifPresent(NeoApoli.LOGGER::warn);
						}

						else {
							NeoApoli.LOGGER.warn("Power instance of {} has changed. Its data won't be recovered!", reference.asDisplayString(false));
						}

						this.instances.put(entry, instance);
						this.sources.put(entry, sources);

					}
					case DataResult.Error<PowerEntry<?>> error ->
						NeoApoli.LOGGER.warn("Error decoding {} from cardinal_components.\"{}\".powers[{}] of entity {} (skipping): {}", reference.asDisplayString(false), NeoApoliEntityComponents.POWERS.getId(), index, holder.getName(), error.message());
				}

			}

			catch (Exception e) {
				NeoApoli.LOGGER.warn("Error decoding power NBT element ({}) at cardinal_components.\"{}\".powers[{}] (skipping): {}", powerNbt, NeoApoliEntityComponents.POWERS.getId(), index, e);
			}

		}

	}

	@Override
	public void writeToNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {

		RegistryOps<Tag> nbtOps = provider.createSerializationContext(NbtOps.INSTANCE);
		ListTag powersNbt = new ListTag();

		this.instances.forEach((entry, instance) -> {

			Set<ResourceLocation> sources = this.sources.getOrDefault(entry, Set.of());
			Tag encodedData = instance.encodeData(nbtOps)
				.mapError(error -> "Error trying to encode data of " + entry.reference().asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (defaulting to empty NBT): " + error)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.orElseGet(nbtOps::emptyMap);

			Data<Tag> data = new Data<>(entry.reference(), entry.power().getType(), sources, new Dynamic<>(nbtOps, encodedData));
			Data.CODEC.encodeStart(nbtOps, data)
				.mapError(error -> "Error trying to encode " + entry.reference().asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (skipping): " + error)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.ifPresent(powersNbt::add);

		});

		compoundTag.put("powers", powersNbt);

	}

	@Override
	public void applySyncPacket(RegistryFriendlyByteBuf buf) {

		byte updateId = buf.readByte();

		switch (updateId) {
			case GRANT_POWERS_UPDATE_ID ->
				decodeChanges(holder, buf).forEach((source, entries) -> entries.forEach(entry -> grantPowerInternal(entry, source)));
			case REVOKE_POWERS_UPDATE_ID ->
				decodeChanges(holder, buf).forEach((source, entries) -> entries.forEach(entry -> revokePowerInternal(entry, source)));
			default ->
				PowersComponent.super.applySyncPacket(buf);
		}

	}

	@Override
	public void writeSyncPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient) {

		buf.writeByte(-1);
		PowersComponent.super.writeSyncPacket(buf, recipient);

		this.grantedPowers.clear();
		this.revokedPowers.clear();

	}

	@Override
	public List<PowerEntry<?>> getAll(boolean includingSubPowers) {

		List<PowerEntry<?>> collected = new ObjectArrayList<>();
		this.instances.keySet().forEach(entry -> {

			if (includingSubPowers || !entry.isSubPower()) {
				collected.add(entry);
			}

		});

		return collected;

	}


	@Override
	public List<PowerEntry<?>> getAllFromSource(ResourceLocation source) {

		List<PowerEntry<?>> collected = new ObjectArrayList<>();
		this.sources.forEach((entry, sources) -> {

			if (sources.contains(source)) {
				collected.add(entry);
			}

		});

		return collected;

	}

	@Override
	public Set<ResourceLocation> getSources(PowerEntry<?> entry) {
		return sources.containsKey(entry)
			? new ObjectOpenHashSet<>(sources.get(entry))
			: new ObjectOpenHashSet<>();
	}


	@NotNull
	public Power.Instance<?> getInstance(PowerEntry<?> entry) {
		return Objects.requireNonNull(this.getNullableInstance(entry), "Entity " + holder.getName().getString() + " didn't have " + entry.reference().asDisplayString(false) + " granted!");
	}

	@Nullable
	public Power.Instance<?> getNullableInstance(PowerEntry<?> entry) {
		return this.instances.get(entry);
	}


	@Override
	public boolean hasInstance(PowerEntry<?> entry, ResourceLocation source) {
		return this.sources.getOrDefault(entry, new ObjectOpenHashSet<>()).contains(source);
	}

	@Override
	public boolean hasInstance(PowerEntry<?> entry) {
		return this.instances.containsKey(entry);
	}


	@Override
	public <I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass, Predicate<I> instanceFilter) {

		List<I> collected = new ObjectArrayList<>();
		this.instances.values().forEach(instance -> {

			if (instanceClass.isInstance(instance)) {

				I casted = instanceClass.cast(instance);

				if (instanceFilter.test(casted)) {
					collected.add(casted);
				}

			}

		});

		return collected;

	}

	@Override
	public List<Power.Instance<?>> getAllInstances() {
		return new ObjectArrayList<>(this.instances.values());
	}


	@Override
	public <I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass, Predicate<I> instanceFilter) {

		for (var instance : this.instances.values()) {

			if (instanceClass.isInstance(instance) && instanceFilter.test(instanceClass.cast(instance))) {
				return true;
			}

		}

		return false;

	}


	@Override
	public boolean grantPower(PowerEntry<?> entry, ResourceLocation source) {
		return !holder.level().isClientSide()
			&& this.grantPowerInternal(entry, source);
	}

	@Override
	public boolean grantPowerImmediately(PowerEntry<?> entry, ResourceLocation source) {

		boolean result = this.grantPower(entry, source);
		this.updateGrantedPowers();

		return result;

	}

	private boolean grantPowerInternal(PowerEntry<?> entry, ResourceLocation source) {

		List<Power.Instance<?>> addedPowers = new ObjectArrayList<>();
		List<Power.Instance<?>> grantedPowers = new ObjectArrayList<>();

		boolean granted = this.grantPowerRecursively(entry, source, addedPowers::add, grantedPowers::add);

		addedPowers.forEach(Power.Instance::onAdded);
		grantedPowers.forEach(Power.Instance::onGranted);

		return granted;

	}

	private boolean grantPowerRecursively(PowerEntry<?> entry, ResourceLocation source, Consumer<Power.Instance<?>> onAdded, Consumer<Power.Instance<?>> onGranted) {

		Set<ResourceLocation> sources = this.sources.computeIfAbsent(entry, k -> new ObjectOpenHashSet<>());
		boolean contains = sources.contains(source);

		if (contains) {
			return false;
		}

		Power power = entry.power();
		Power.Instance<?> instance = power.createInstance(holder);

		sources.add(source);
		onAdded.accept(instance);

		if (this.instances.put(entry, instance) == null) {
			onGranted.accept(instance);
		}

		if (power instanceof MultiplePower multiplePower) {

			for (PowerEntry<?> subPower : multiplePower.getSubPowers()) {
				this.grantPowerRecursively(subPower, source, onAdded, onGranted);
			}

		}

		if (!holder.level().isClientSide()) {
			this.grantedPowers.computeIfAbsent(source, k -> new ObjectOpenHashSet<>()).add(entry);
		}

		return true;

	}


	@Override
	public boolean revokePower(PowerEntry<?> entry, ResourceLocation source) {
		return !holder.level().isClientSide()
			&& this.revokePowerInternal(entry, source);
	}

	@Override
	public boolean revokePowerImmediately(PowerEntry<?> entry, ResourceLocation source) {

		boolean result = this.revokePower(entry, source);
		this.updateRevokedPowers();

		return result;

	}

	private boolean revokePowerInternal(PowerEntry<?> entry, ResourceLocation source) {

		List<PowerEntry<?>> revokedPowers = new ObjectArrayList<>();
		boolean result = this.revokePowerRecursively(entry, source, revokedPowers::add);

		instances.keySet().removeIf(revokedPowers::contains);
		sources.keySet().removeIf(revokedPowers::contains);

		return result;

	}

	private boolean revokePowerRecursively(PowerEntry<?> entry, ResourceLocation source, Consumer<PowerEntry<?>> onRevoked) {

		Set<ResourceLocation> sources = this.sources.getOrDefault(entry, new ObjectOpenHashSet<>());
		boolean removed = sources.remove(source);

		if (!removed || !instances.containsKey(entry)) {
			return false;
		}

		Power.Instance<?> instance = instances.get(entry);
		instance.onRemoved();

		if (sources.isEmpty()) {
			instance.onRevoked();
			onRevoked.accept(entry);
		}

		if (instance.getPower() instanceof MultiplePower multiplePower) {

			for (var subPower : multiplePower.getSubPowers()) {
				this.revokePowerRecursively(subPower, source, onRevoked);
			}

		}

		if (!holder.level().isClientSide()) {
			this.revokedPowers.computeIfAbsent(source, k -> new ObjectOpenHashSet<>()).add(entry);
		}

		return true;

	}


	@Override
	public void updateGrantedPowers() {

		if (holder.level().isClientSide() || grantedPowers.isEmpty()) {
			return;
		}

		NeoApoliEntityComponents.POWERS.sync(holder, (buf, recipient) -> encodeChanges(holder, GRANT_POWERS_UPDATE_ID, buf, this.grantedPowers));
		this.grantedPowers.clear();

	}

	@Override
	public void updateRevokedPowers() {

		if (holder.level().isClientSide() || revokedPowers.isEmpty()) {
			return;
		}

		NeoApoliEntityComponents.POWERS.sync(holder, (buf, recipient) -> encodeChanges(holder, REVOKE_POWERS_UPDATE_ID, buf, this.revokedPowers));
		this.revokedPowers.clear();

	}


	private static void encodeChanges(Entity ignored, byte id, RegistryFriendlyByteBuf buf, Map<ResourceLocation, Set<PowerEntry<?>>> powers) {
		buf.writeByte(id);
		buf.writeMap(
			powers,
			FriendlyByteBuf::writeResourceLocation,
			(valueBuf, entries) -> buf.writeCollection(
				entries,
				(elementBuf, entry) -> PowerReference.STREAM_CODEC.encode(elementBuf, entry.reference())
			)
		);
	}

	private static Map<ResourceLocation, Set<PowerEntry<?>>> decodeChanges(Entity holder, RegistryFriendlyByteBuf buf) {
		return buf.readMap(
			FriendlyByteBuf::readResourceLocation,
			valueBuf -> {

				Set<PowerEntry<?>> entries = new ObjectOpenHashSet<>();
				int size = valueBuf.readVarInt();

				for (int i = 0; i < size; i++) {

					PowerReference reference = PowerReference.STREAM_CODEC.decode(valueBuf);

					PowerManager.getEntryAsResult(reference)
						.ifError(error -> NeoApoli.LOGGER.warn("Received unknown {} while partially updating power component of entity {}! Skipping...", reference.asDisplayString(false), holder.getName().getString()))
						.ifSuccess(entries::add);

				}

				return entries;

			}
		);
	}

	private static void update(Entity entity) {

		PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(entity);
		List<PowerEntry<?>> oldEntries = powersComponent.getAll();

		int mismatches = 0;

		for (var oldEntry : oldEntries) {

			PowerReference reference = oldEntry.reference();
			Set<ResourceLocation> sources = powersComponent.getSources(oldEntry);

			if (!PowerManager.contains(reference)) {

				NeoApoli.LOGGER.error("Removed unregistered {} from entity {}!", reference.asDisplayString(false), entity.getName().getString());

				for (var source : sources) {
					powersComponent.revokePower(oldEntry, source);
				}

			}

			else {

				Power oldPower = oldEntry.power();
				PowerEntry<?> newEntry = PowerManager.getEntry(reference);

				if (!Objects.equals(oldPower, newEntry.power())) {

					NeoApoli.LOGGER.warn("{} from entity {} has mismatched data! Updating...", reference.asDisplayString(), entity.getName().getString());
					Power.Instance<?> oldInstance = powersComponent.getInstance(oldEntry);

					for (var source : sources) {
						powersComponent.revokePower(oldEntry, source);
						powersComponent.grantPower(newEntry, source);
					}

					Power.Instance<?> newInstance = powersComponent.getInstance(newEntry);
					mismatches++;

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
			NeoApoli.LOGGER.warn("Finished updating {} power(s) with mismatched data from entity {}!", mismatches, entity.getName().getString());
		}

		NeoApoliEntityComponents.POWERS.sync(entity);

	}

	public record Data<T>(PowerReference reference, PowerType<?> type, Set<ResourceLocation> sources, Dynamic<T> encoded) {

		public static final Codec<Data<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PowerReference.CODEC.fieldOf("id").forGetter(Data::reference),
			PowerType.CODEC.fieldOf("type").forGetter(Data::type),
			NeoApoliCodecs.MUTABLE_NON_EMPTY_IDENTIFIER_SET.fieldOf("sources").forGetter(Data::sources),
			Codec.PASSTHROUGH.fieldOf("data").forGetter(Data::encoded)
		).apply(instance, Data::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Data<?>> STREAM_CODEC = StreamCodec.composite(
			PowerReference.STREAM_CODEC, Data::reference,
			PowerType.STREAM_CODEC, Data::type,
			NeoApoliStreamCodecs.MUTABLE_NON_EMPTY_IDENTIFIER_SET, Data::sources,
			NeoApoliStreamCodecs.REGISTRY_PASSTHROUGH, Data::encoded,
			Data::new
		);

	}

	static {

		ServerEntityEvents.ENTITY_LOAD.register(ID, (entity, world) -> {

			if (!(entity instanceof Player)) {
				update(entity);
			}

		});

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(PowerManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, (player, joined) -> update(player));

	}

}
