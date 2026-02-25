package io.github.eggohito.neo_apoli.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizePowerDataS2CPacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.PowerReference;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class PowersComponentImpl implements PowersComponent {

	private static final StreamCodec<RegistryFriendlyByteBuf, Object2BooleanMap<PowerReference>> REF_AND_CALLBACK_CODEC = ByteBufCodecs.map(Object2BooleanOpenHashMap::new, PowerReference.STREAM_CODEC, ByteBufCodecs.BOOL);
	private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Object2BooleanMap<PowerReference>>> UPDATE_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, REF_AND_CALLBACK_CODEC);

	private static final byte GRANT_POWERS_UPDATE_ID = 0;
	private static final byte REVOKE_POWERS_UPDATE_ID = 1;

	private final Map<PowerReference, Power.Instance<?>> instances;
	private final Map<PowerReference, Set<ResourceLocation>> sources;

	private final Map<ResourceLocation, Object2BooleanMap<PowerReference>> grantedPowers;
	private final Map<ResourceLocation, Object2BooleanMap<PowerReference>> revokedPowers;

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

		RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
		ListTag powersNbt = compoundTag.getListOrEmpty("powers");

		this.instances.clear();
		this.sources.clear();

		ListIterator<Tag> listIterator = powersNbt.listIterator();
		while (listIterator.hasNext()) {

			int index = listIterator.nextIndex();
			Tag powerNbt = listIterator.next();

			try {

				Data<?> data = Data.CODEC
					.parse(ops, powerNbt)
					.getOrThrow();

				PowerReference reference = data.reference();
				DataResult<Power> powerResult = PowerManager.getAsResult(reference);

				switch (powerResult) {
					case DataResult.Success<Power> success -> {

						Dynamic<Tag> encodedData = data.encoded().convert(ops);
						Set<ResourceLocation> sources = data.sources();

						Power power = success.value();
						Power.Instance<?> instance = power.createInstance(holder);

						if (Objects.equals(data.type(), power.getType())) {
							instance.decodeData(ops, encodedData.getValue())
								.mapError(error -> "Error decoding data of " + reference.asDisplayString(false) + " from NBT (skipping): " + error)
								.error()
								.map(DataResult.Error::message)
								.ifPresent(NeoApoli.LOGGER::warn);
						}

						else {
							NeoApoli.LOGGER.warn("Power instance of {} has changed. Its data won't be recovered!", reference.asDisplayString(false));
						}

						this.instances.put(reference, instance);
						this.sources.put(reference, sources);

					}
					case DataResult.Error<Power> error ->
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

		RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
		ListTag powersNbt = new ListTag();

		this.instances.forEach((reference, instance) -> {

			Set<ResourceLocation> sources = this.sources.getOrDefault(reference, Set.of());
			Tag encodedData = instance.encodeData(ops)
				.mapError(error -> "Error trying to encode data of " + reference.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (defaulting to empty NBT): " + error)
				.resultOrPartial(NeoApoli.LOGGER::warn)
				.orElseGet(ops::emptyMap);

			Data<Tag> data = new Data<>(reference, instance.getPower().getType(), sources, new Dynamic<>(ops, encodedData));
			Data.CODEC.encodeStart(ops, data)
				.mapError(error -> "Error trying to encode " + reference.asDisplayString(false) + " to NBT of entity " + holder.getName().getString() + " (skipping): " + error)
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
				UPDATE_CODEC.decode(buf).forEach((source, entries) -> entries.forEach((reference, invokeCallbacks) -> grantPowerInternal(reference, source, invokeCallbacks)));
			case REVOKE_POWERS_UPDATE_ID ->
				UPDATE_CODEC.decode(buf).forEach((source, entries) -> entries.forEach((reference, invokeCallbacks) -> revokePowerInternal(reference, source, invokeCallbacks)));
			case -1 ->
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
	public Set<PowerReference> getAllReferences() {
		return new ObjectOpenHashSet<>(this.instances.keySet());
	}

	@Override
	public Set<ResourceLocation> getAllSources() {

		Set<ResourceLocation> collected = new ObjectOpenHashSet<>();
		this.sources.values().forEach(collected::addAll);

		return collected;

	}

	@Override
	public List<PowerEntry<?>> getAll(boolean includingSubPowers) {

		List<PowerEntry<?>> collected = new ObjectArrayList<>();
		this.instances.keySet().forEach(reference -> {

			if (PowerManager.contains(reference) && (includingSubPowers || !reference.isSubPower())) {
				collected.add(PowerManager.getEntry(reference));
			}

		});

		return collected;

	}


	@Override
	public List<PowerEntry<?>> getAllFromSource(ResourceLocation source) {

		List<PowerEntry<?>> collected = new ObjectArrayList<>();
		this.sources.forEach((reference, sources) -> {

			if (PowerManager.contains(reference) && sources.contains(source)) {
				collected.add(PowerManager.getEntry(reference));
			}

		});

		return collected;

	}

	@Override
	public Set<ResourceLocation> getSources(PowerReference reference) {
		return sources.containsKey(reference)
			? new ObjectOpenHashSet<>(sources.get(reference))
			: new ObjectOpenHashSet<>();
	}


	public Power.@NotNull Instance<?> getInstance(PowerReference reference) {
		return Objects.requireNonNull(instances.get(reference), "Entity " + holder.getName().getString() + " didn't have " + reference.asDisplayString(false) + " granted!");
	}

	@Override
	public boolean hasInstance(PowerReference reference, ResourceLocation source) {
		return this.sources.getOrDefault(reference, new ObjectOpenHashSet<>()).contains(source);
	}

	@Override
	public boolean hasInstance(PowerReference reference) {
		return this.instances.containsKey(reference);
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
	public boolean grantPower(PowerReference reference, ResourceLocation source, boolean invokeCallbacks) {
		return !holder.level().isClientSide()
			&& this.grantPowerInternal(reference, source, invokeCallbacks);
	}

	private boolean grantPowerInternal(PowerReference reference, ResourceLocation source, boolean invokeCallbacks) {

		List<Power.Instance<?>> addedPowers = new ObjectArrayList<>();
		List<Power.Instance<?>> grantedPowers = new ObjectArrayList<>();

		boolean granted = this.grantPowerRecursively(reference, source, addedPowers::add, grantedPowers::add, invokeCallbacks);

		addedPowers.forEach(Power.Instance::onAdded);
		grantedPowers.forEach(Power.Instance::onGranted);

		return granted;

	}

	private boolean grantPowerRecursively(PowerReference reference, ResourceLocation source, Consumer<Power.Instance<?>> onAdded, Consumer<Power.Instance<?>> onGranted, boolean invokeCallbacks) {

		if (!PowerManager.contains(reference)) {
			return false;
		}

		Set<ResourceLocation> sources = this.sources.computeIfAbsent(reference, k -> new ObjectOpenHashSet<>());
		boolean firstTimeGranting = !this.instances.containsKey(reference);

		if (!sources.add(source)) {
			return false;
		}

		Power power = PowerManager.get(reference);
		Power.Instance<?> instance = this.instances.computeIfAbsent(reference, k -> power.createInstance(holder));

		if (power instanceof MultiplePower multiplePower) {

			for (PowerEntry<?> subPower : multiplePower.getSubPowers()) {
				this.grantPowerRecursively(subPower.reference(), source, onAdded, onGranted, invokeCallbacks);
			}

		}

		if (invokeCallbacks) {

			if (firstTimeGranting) {
				onGranted.accept(instance);
			}

			onAdded.accept(instance);

		}

		if (!holder.level().isClientSide()) {
			this.grantedPowers
				.computeIfAbsent(source, k -> new Object2BooleanOpenHashMap<>())
				.put(reference, invokeCallbacks);
		}

		return true;

	}


	@Override
	public boolean revokePower(PowerReference reference, ResourceLocation source, boolean invokeCallbacks) {
		return !holder.level().isClientSide()
			&& this.revokePowerInternal(reference, source, invokeCallbacks);
	}

	private boolean revokePowerInternal(PowerReference reference, ResourceLocation source, boolean invokeCallbacks) {

		List<PowerReference> revokedPowers = new ObjectArrayList<>();
		boolean result = this.revokePowerRecursively(reference, source, revokedPowers::add, invokeCallbacks);

		instances.keySet().removeIf(revokedPowers::contains);
		sources.keySet().removeIf(revokedPowers::contains);

		return result;

	}

	private boolean revokePowerRecursively(PowerReference reference, ResourceLocation source, Consumer<PowerReference> onRevoked, boolean invokeCallbacks) {

		Set<ResourceLocation> sources = this.sources.getOrDefault(reference, new ObjectOpenHashSet<>());
		if (!sources.remove(source) || !instances.containsKey(reference)) {
			return false;
		}

		Power.Instance<?> instance = instances.get(reference);
		boolean revoked = sources.isEmpty();

		if (instance.getPower() instanceof MultiplePower multiplePower) {

			for (var subPower : multiplePower.getSubPowers()) {
				this.revokePowerRecursively(subPower.reference(), source, onRevoked, invokeCallbacks);
			}

		}

		if (revoked) {

			onRevoked.accept(reference);

			if (invokeCallbacks) {
				instance.onRevoked();
			}

		}

		if (invokeCallbacks) {
			instance.onRemoved();
		}

		if (!holder.level().isClientSide()) {
			this.revokedPowers
				.computeIfAbsent(source, k -> new Object2BooleanOpenHashMap<>())
				.put(reference, invokeCallbacks);
		}

		return true;

	}

	@Override
	public void checkForUpdates() {

		if (holder.level().isClientSide()) {
			return;
		}

		if (!revokedPowers.isEmpty()) {

			NeoApoliEntityComponents.POWERS.sync(holder, (buf, recipient) -> {
				buf.writeByte(REVOKE_POWERS_UPDATE_ID);
				UPDATE_CODEC.encode(buf, revokedPowers);
			});

			this.revokedPowers.clear();

		}

		if (!grantedPowers.isEmpty()) {

			NeoApoliEntityComponents.POWERS.sync(holder, (buf, recipient) -> {
				buf.writeByte(GRANT_POWERS_UPDATE_ID);
				UPDATE_CODEC.encode(buf, grantedPowers);
			});

			this.grantedPowers.clear();

		}

	}

	private static void update(Entity entity, boolean joined) {

		if (joined) {
			return;
		}

		PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(entity);
		RegistryOps<Tag> nbtOps = entity.registryAccess().createSerializationContext(NbtOps.INSTANCE);

		Map<PowerReference, Tag> pendingDataSync = new Object2ObjectOpenHashMap<>();
		Object2BooleanMap<PowerReference> differentPowerTypes = new Object2BooleanOpenHashMap<>();

		//  Replace old instances of the granted powers with new ones, and store its data to be synced and
		//  transferred later
		for (var reference : powersComponent.getAllReferences()) {

			if (!PowerManager.contains(reference)) {

				for (var source : powersComponent.getSources(reference)) {
					powersComponent.revokePower(reference, source);
				}

				NeoApoli.LOGGER.warn("Removed unregistered {} from entity {}!", reference.asDisplayString(false), entity.getName().getString());

			}

			else {

				Power.Instance<?> oldInstance = powersComponent.getInstance(reference);
				oldInstance.encodeData(nbtOps)
					.resultOrPartial(error -> NeoApoli.LOGGER.warn("Couldn't fully encode old data of {} from entity {} during the transfer process: {}", reference.asDisplayString(false), entity.getName().getString(), error))
					.ifPresent(tag -> pendingDataSync.put(reference, tag));

				for (var source : powersComponent.getSources(reference)) {
					powersComponent.revokePowerNoCallback(reference, source);
					powersComponent.grantPowerNoCallback(reference, source);
				}

				differentPowerTypes.put(reference, !oldInstance.getClass().isInstance(powersComponent.getInstance(reference)));

			}

		}

		//  Transfer the stored old data of the granted powers
		for (var reference : powersComponent.getAllReferences()) {

			if (!pendingDataSync.containsKey(reference)) {
				continue;
			}

			Tag oldData = pendingDataSync.get(reference);
			boolean differentPowerType = differentPowerTypes.getOrDefault(reference, false);

			if (differentPowerType) {
				NeoApoli.LOGGER.warn("Couldn't transfer old data of {} from entity {}, as it's now using a different power type!", reference.asDisplayString(false), entity.getName().getString());
			}

			else {
				powersComponent.getInstance(reference)
					.decodeData(nbtOps, oldData)
					.resultOrPartial(error -> NeoApoli.LOGGER.warn("Couldn't transfer data of {} from entity {}: {}", reference.asDisplayString(false), entity.getName().getString(), error));
			}

		}

		powersComponent.checkForUpdates();

		if (!pendingDataSync.isEmpty()) {

			SynchronizePowerDataS2CPacket packet = SynchronizePowerDataS2CPacket.bulk(entity.getId(), nbtOps, pendingDataSync);

			for (var tracker : MiscUtil.getTrackingPlayers(entity)) {
				ServerPlayNetworking.send(tracker, packet);
			}

		}

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

		ServerEntityEvents.ENTITY_LOAD.register(ID, (entity, Level) -> update(entity, entity instanceof Player));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(PowerManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, PowersComponentImpl::update);

	}

}
