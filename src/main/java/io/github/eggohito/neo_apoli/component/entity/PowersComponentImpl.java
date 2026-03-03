package io.github.eggohito.neo_apoli.component.entity;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.NeoApoli;
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
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class PowersComponentImpl implements PowersComponent {

	private static final StreamCodec<RegistryFriendlyByteBuf, Object2BooleanMap<PowerReference>> REF_AND_CALLBACK_CODEC = ByteBufCodecs.map(Object2BooleanLinkedOpenHashMap::new, PowerReference.STREAM_CODEC, ByteBufCodecs.BOOL);
	private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Object2BooleanMap<PowerReference>>> UPDATE_CODEC = ByteBufCodecs.map(Object2ObjectLinkedOpenHashMap::new, ResourceLocation.STREAM_CODEC, REF_AND_CALLBACK_CODEC);

	private static final byte GRANT_POWERS_UPDATE_ID = 0;
	private static final byte REVOKE_POWERS_UPDATE_ID = 1;

	private final Object2ObjectMap<PowerReference, Power.Instance<?>> instances;
	private final SetMultimap<PowerReference, ResourceLocation> sources;

	private final Map<ResourceLocation, Object2BooleanMap<PowerReference>> grantedPowers;
	private final Map<ResourceLocation, Object2BooleanMap<PowerReference>> revokedPowers;

	private final Entity holder;

	public PowersComponentImpl(Entity holder) {
		this.instances = new Object2ObjectLinkedOpenHashMap<>();
		this.sources = LinkedHashMultimap.create();
		this.grantedPowers = new Object2ObjectLinkedOpenHashMap<>();
		this.revokedPowers = new Object2ObjectLinkedOpenHashMap<>();
		this.holder = holder;
	}

	@Override
	public void tick() {

		for (var instance : instances.values()) {

			if (instance.shouldTick(holder)) {
				instance.onTick(holder);
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

				Packed<?> packed = Packed.CODEC
					.parse(ops, powerNbt)
					.getOrThrow();

				PowerReference reference = packed.reference();
				DataResult<Power> powerResult = PowerManager.getAsResult(reference);

				switch (powerResult) {
					case DataResult.Success<Power> success -> {

						Dynamic<Tag> encodedData = packed.data().convert(ops);
						Set<ResourceLocation> sources = packed.sources();

						Power power = success.value();
						Power.Instance<?> instance = power.createInstance();

						if (Objects.equals(packed.type(), power.getType())) {
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
						this.sources.putAll(reference, sources);

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

			Set<ResourceLocation> sources = this.sources.get(reference);
			if (sources.isEmpty()) {
				NeoApoli.LOGGER.warn("Tried encoding {} to NBT of entity {}, which didn't have any sources!", reference.asDisplayString(false), holder.getName().getString());
			}

			else {

				Tag data = instance.encodeData(ops)
					.resultOrPartial(error -> NeoApoli.LOGGER.warn("Error trying to encode data of {} to NBT of entity {} (defaulting to empty NBT): {}", reference.asDisplayString(false), holder.getName().getString(), error))
					.orElseGet(ops::emptyMap);

				Packed.CODEC.encodeStart(ops, new Packed<>(reference, instance.getPower().getType(), sources, new Dynamic<>(ops, data)))
					.resultOrPartial(error -> NeoApoli.LOGGER.warn("Error trying to encode {} to NBT of entity {} (skipping): {}", reference.asDisplayString(false), holder.getName().getString(), error))
					.ifPresent(powersNbt::add);

			}

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
		return new ObjectLinkedOpenHashSet<>(this.instances.keySet());
	}

	@Override
	public Set<ResourceLocation> getAllSources() {
		return new ObjectLinkedOpenHashSet<>(this.sources.values());
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
		this.sources.asMap().forEach((reference, sources) -> {

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
		return sources.containsKey(reference)
			&& sources.get(reference).contains(source);
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

		addedPowers.forEach(instance -> instance.onAdded(holder));
		grantedPowers.forEach(instance -> instance.onGranted(holder));

		return granted;

	}

	private boolean grantPowerRecursively(PowerReference reference, ResourceLocation source, Consumer<Power.Instance<?>> onAdded, Consumer<Power.Instance<?>> onGranted, boolean invokeCallbacks) {

		if (!PowerManager.contains(reference)) {
			return false;
		}

		Set<ResourceLocation> sources = this.sources.get(reference);
		boolean firstTimeGranting = !this.instances.containsKey(reference);

		if (!sources.add(source)) {
			return false;
		}

		Power power = PowerManager.get(reference);
		Power.Instance<?> instance = this.instances.computeIfAbsent(reference, k -> power.createInstance());

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

		if (!sources.remove(reference, source) || !instances.containsKey(reference)) {
			return false;
		}

		Power.Instance<?> instance = instances.get(reference);
		boolean revoked = sources.get(reference).isEmpty();

		if (instance.getPower() instanceof MultiplePower multiplePower) {

			for (var subPower : multiplePower.getSubPowers()) {
				this.revokePowerRecursively(subPower.reference(), source, onRevoked, invokeCallbacks);
			}

		}

		if (revoked) {

			onRevoked.accept(reference);

			if (invokeCallbacks) {
				instance.onRevoked(holder);
			}

		}

		if (invokeCallbacks) {
			instance.onRemoved(holder);
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

		if (joined || entity.level().isClientSide()) {
			return;
		}

		PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(entity);
		RegistryOps<Tag> ops = entity.registryAccess().createSerializationContext(NbtOps.INSTANCE);

		Map<PowerReference, Tag> pendingDataSync = new Object2ObjectOpenHashMap<>();
		Map<PowerReference, PowerType<?>> oldPowerTypes = new Object2ObjectOpenHashMap<>();

		//  Revoke all unregistered powers, and cache all the old data of the existing powers from the entity
		for (var reference : powersComponent.getAllReferences()) {

			if (!PowerManager.contains(reference)) {

				for (var source : powersComponent.getSources(reference)) {
					powersComponent.revokePower(reference, source);
				}

				NeoApoli.LOGGER.warn("Removed unregistered {} from entity {}!", reference.asDisplayString(false), entity.getName().getString());

			}

			else {

				Power.Instance<?> oldInstance = powersComponent.getInstance(reference);
				PowerType<?> oldPowerType = oldInstance.getPower().getType();

				oldPowerTypes.put(reference, oldPowerType);
				oldInstance.encodeData(ops)
					.resultOrPartial(error -> NeoApoli.LOGGER.warn("Couldn't fully encode old data of {} from entity {} during the transfer process: {}", reference.asDisplayString(false), entity.getName().getString(), error))
					.ifPresent(tag -> pendingDataSync.put(reference, tag));

			}

		}

		//  Re-grant all the existing powers and restore its old data
		for (var reference : powersComponent.getAllReferences()) {

			for (var source : powersComponent.getSources(reference)) {
				powersComponent.revokePowerNoCallback(reference, source);
				powersComponent.grantPowerNoCallback(reference, source);
			}

			if (pendingDataSync.containsKey(reference)) {

				Tag oldData = pendingDataSync.get(reference);
				Power.Instance<?> newInstance = powersComponent.getInstance(reference);

				if (Objects.equals(oldPowerTypes.get(reference), newInstance.getPower().getType())) {
					newInstance
						.decodeData(ops, oldData)
						.resultOrPartial(error -> NeoApoli.LOGGER.warn("Couldn't transfer data of {} from entity {}: {}", reference.asDisplayString(false), entity.getName().getString(), error));
				}

				else {
					NeoApoli.LOGGER.warn("Couldn't transfer old data of {} from entity {}, as it's now using a different power type!", reference.asDisplayString(false), entity.getName().getString());
				}

			}

		}

		powersComponent.checkForUpdates();

		if (!pendingDataSync.isEmpty()) {
			MiscUtil.sendToTracking(entity, SynchronizePowerDataS2CPacket.bulk(entity.getId(), ops, pendingDataSync));
		}

	}

	static {

		ServerEntityEvents.ENTITY_LOAD.register(ID, (entity, Level) -> update(entity, entity instanceof Player));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(PowerManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, PowersComponentImpl::update);

	}

}
