package io.github.eggohito.neo_apoli.impl.power;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.api.power.PowersAttachment;
import io.github.eggohito.neo_apoli.attachment.NeoApoliEntityAttachments;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizePowerDataS2CPacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.PowerReference;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public final class PowersImpl implements Powers {

	private static final StreamCodec<ByteBuf, Object2BooleanMap<PowerReference>> REFERENCE_AND_CALLBACK_STREAM_CODEC = ByteBufCodecs.map(Object2BooleanOpenHashMap::new, PowerReference.STREAM_CODEC, ByteBufCodecs.BOOL);
	private static final StreamCodec<ByteBuf, Object2ObjectMap<ResourceLocation, Object2BooleanMap<PowerReference>>> UPDATE_STREAM_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, REFERENCE_AND_CALLBACK_STREAM_CODEC);

	private final Entity holder;
	private final PowersAttachment.Mutable mutableAttachment;

	private final Object2ObjectMap<ResourceLocation, Object2BooleanMap<PowerReference>> grantedPowers = new Object2ObjectLinkedOpenHashMap<>();
	private final Object2ObjectMap<ResourceLocation, Object2BooleanMap<PowerReference>> revokedPowers = new Object2ObjectLinkedOpenHashMap<>();

	public PowersImpl(Entity holder, PowersAttachment attachment) {

		this.holder = holder;
		this.mutableAttachment = new PowersAttachment.Mutable(attachment);

		attachment.decodingErrors().ifPresent(errors -> NeoApoli.LOGGER.warn("Found errors while decoding powers attachment on entity {}: {}", holder.getName().getString(), errors));

	}

	@Override
	public Set<PowerReference> getAllReferences() {
		return new ObjectLinkedOpenHashSet<>(mutableAttachment.instances().keySet());
	}

	@Override
	public Set<ResourceLocation> getAllSources() {
		return new ObjectLinkedOpenHashSet<>(mutableAttachment.sources().values());
	}

	@Override
	public List<PowerEntry<?>> getAll(boolean includeSubPowers) {

		List<PowerEntry<?>> result = new ObjectArrayList<>();
		mutableAttachment.instances().keySet().forEach(reference -> PowerManager.getEntryAsResult(reference)
			.result()
			.filter(entry -> includeSubPowers || !entry.isSubPower())
			.ifPresent(result::add));

		return result;

	}

	@Override
	public List<PowerEntry<?>> getAllFromSource(ResourceLocation source) {

		List<PowerEntry<?>> result = new ObjectArrayList<>();
		mutableAttachment.sources().asMap().forEach((reference, sources) -> PowerManager.getEntryAsResult(reference)
			.result()
			.filter(entry -> sources.contains(source))
			.ifPresent(result::add));

		return result;

	}

	@Override
	public Set<ResourceLocation> getSources(PowerReference reference) {
		return new ObjectOpenHashSet<>(mutableAttachment.sources().values());
	}

	@Override
	public @NotNull Power.Instance<?> getInstance(PowerReference reference) {
		return Objects.requireNonNull(mutableAttachment.instances().get(reference), "Entity " + holder.getName().getString() + " didn't have " + reference.asDisplayString(false) + " granted!");
	}

	@Override
	public boolean hasInstance(PowerReference reference, ResourceLocation source) {
		return mutableAttachment.sources().get(reference).contains(source);
	}

	@Override
	public boolean hasInstance(PowerReference reference) {
		return mutableAttachment.instances().containsKey(reference);
	}

	@Override
	public <I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass, Predicate<I> instanceFilter) {

		List<I> result = new ObjectArrayList<>();
		mutableAttachment.instances().values().forEach(instance -> {

			if (instanceClass.isInstance(instance)) {

				I casted = instanceClass.cast(instance);

				if (instanceFilter.test(casted)) {
					result.add(casted);
				}

			}

		});

		return result;

	}

	@Override
	public List<Power.Instance<?>> getAllInstances() {
		return new ObjectArrayList<>(mutableAttachment.instances().values());
	}

	@Override
	public <I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass, Predicate<I> instanceFilter) {

		for (var instance : mutableAttachment.instances().values()) {

			if (instanceClass.isInstance(instance) && instanceFilter.test(instanceClass.cast(instance))) {
				return true;
			}

		}

		return false;

	}

	@Override
	public boolean grant(PowerReference reference, ResourceLocation source, boolean invokeCallbacks) {
		return !holder.level().isClientSide()
			&& this.grantPowerInternal(reference, source, invokeCallbacks);
	}

	@Override
	public boolean revoke(PowerReference reference, ResourceLocation source, boolean invokeCallbacks) {
		return !holder.level().isClientSide()
			&& this.revokePowerInternal(reference, source, invokeCallbacks);
	}

	@Override
	public void update() {

		if (holder.level().isClientSide()) {
			return;
		}

		if (!revokedPowers.isEmpty() || !grantedPowers.isEmpty()) {

			if (!revokedPowers.isEmpty()) {
				MiscUtil.sendToTracking(holder, new RevokeS2CPacket(holder.getId(), new Object2ObjectLinkedOpenHashMap<>(this.revokedPowers)));
			}

			if (!grantedPowers.isEmpty()) {
				MiscUtil.sendToTracking(holder, new GrantS2CPacket(holder.getId(), new Object2ObjectLinkedOpenHashMap<>(this.grantedPowers)));
			}

		}

		holder.setAttached(NeoApoliEntityAttachments.POWERS, mutableAttachment.toImmutable());

		this.revokedPowers.clear();
		this.grantedPowers.clear();

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

		Set<ResourceLocation> sources = mutableAttachment.sources().get(reference);
		boolean firstTimeGranting = !mutableAttachment.instances().containsKey(reference);

		if (!sources.add(source)) {
			return false;
		}

		Power power = PowerManager.get(reference);
		Power.Instance<?> instance = mutableAttachment.instances().computeIfAbsent(reference, k -> power.createInstance());

		if (power instanceof MultiplePower multiplePower) {

			for (var subPower : multiplePower.getSubPowers()) {
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
				.computeIfAbsent(source, k -> new Object2BooleanLinkedOpenHashMap<>())
				.put(reference, invokeCallbacks);
		}

		return true;

	}

	private boolean revokePowerInternal(PowerReference reference, ResourceLocation source, boolean invokeCallbacks) {

		List<PowerReference> revokedPowers = new ObjectArrayList<>();
		boolean result = this.revokePowerRecursively(reference, source, revokedPowers::add, invokeCallbacks);

		mutableAttachment.instances().keySet().removeIf(revokedPowers::contains);
		mutableAttachment.sources().keySet().removeIf(revokedPowers::contains);

		return result;

	}

	private boolean revokePowerRecursively(PowerReference reference, ResourceLocation source, Consumer<PowerReference> onRevoked, boolean invokeCallbacks) {

		if (!mutableAttachment.sources().remove(reference, source) || !mutableAttachment.instances().containsKey(reference)) {
			return false;
		}

		Power.Instance<?> instance = mutableAttachment.instances().get(reference);
		boolean revoked = mutableAttachment.sources().get(reference).isEmpty();

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
				.computeIfAbsent(source, k -> new Object2BooleanLinkedOpenHashMap<>())
				.put(reference, invokeCallbacks);
		}

		return true;

	}


	private static void update(Entity entity, boolean joined) {

		if (entity.level().isClientSide() || joined || !Powers.has(entity)) {
			return;
		}

		Powers powers = Powers.getOrCreate(entity);
		RegistryOps<Tag> ops = entity.registryAccess().createSerializationContext(NbtOps.INSTANCE);

		Map<PowerReference, Tag> pendingDataSync = new Object2ObjectOpenHashMap<>();
		Map<PowerReference, PowerType<?>> oldTypes = new Object2ObjectOpenHashMap<>();

		//  Revoke all unregistered powers, and cache the old data of those that are on the entity
		for (var reference : powers.getAllReferences()) {

			if (!PowerManager.contains(reference)) {

				for (var source : powers.getSources(reference)) {
					powers.revokeWithCallback(reference, source);
				}

				NeoApoli.LOGGER.warn("Removed unregistered {} from entity {}!", reference.asDisplayString(false), entity.getName().getString());

			}

			else {

				Power.Instance<?> oldInstance = powers.getInstance(reference);
				PowerType<?> oldType = oldInstance.getPower().getType();

				oldTypes.put(reference, oldType);
				oldInstance.encodeData(ops)
					.resultOrPartial(error -> NeoApoli.LOGGER.warn("Couldn't fully encode old data of {} from entity {} during the transfer process: {}", reference.asDisplayString(false), entity.getName().getString(), error))
					.ifPresent(tag -> pendingDataSync.put(reference, tag));

			}

		}

		//  Re-grant all the existing powers and restore its old data
		for (var reference : powers.getAllReferences()) {

			for (var source : powers.getSources(reference)) {
				powers.revokeWithoutCallback(reference, source);
				powers.grantWithoutCallback(reference, source);
			}

			if (pendingDataSync.containsKey(reference)) {

				Tag oldData = pendingDataSync.get(reference);
				Power.Instance<?> newInstance = powers.getInstance(reference);

				if (Objects.equals(oldTypes.get(reference), newInstance.getPower().getType())) {
					newInstance
						.decodeData(ops, oldData)
						.resultOrPartial(error -> NeoApoli.LOGGER.warn("Couldn't transfer data of {} from entity {}: {}", reference.asDisplayString(false), entity.getName().getString(), error));
				}

				else {
					NeoApoli.LOGGER.warn("Couldn't transfer old data of {} from entity {}, as it's now using a different power type!", reference.asDisplayString(false), entity.getName().getString());
				}

			}

		}

		powers.update();

		if (!pendingDataSync.isEmpty()) {
			MiscUtil.sendToTracking(entity, SynchronizePowerDataS2CPacket.bulk(entity.getId(), ops, pendingDataSync));
		}

	}


	public record GrantS2CPacket(int entityId, Object2ObjectMap<ResourceLocation, Object2BooleanMap<PowerReference>> powers) implements CustomPacketPayload {

		public static final Type<GrantS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/grant_powers"));
		public static final StreamCodec<ByteBuf, GrantS2CPacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, GrantS2CPacket::entityId,
			UPDATE_STREAM_CODEC, GrantS2CPacket::powers,
			GrantS2CPacket::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(Level level) {

			Entity holder = level.getEntity(entityId());

			if (holder == null) {
				NeoApoli.LOGGER.warn("Received packet for granting {} power(s) to an unknown entity!", powers().size());
			}

			else {
				PowersImpl powers = (PowersImpl) Powers.getOrCreate(holder);
				powers().forEach((source, entries) -> entries.forEach((reference, invokeCallbacks) -> powers.grantPowerInternal(reference, source, invokeCallbacks)));
			}

		}

	}

	public record RevokeS2CPacket(int entityId, Object2ObjectMap<ResourceLocation, Object2BooleanMap<PowerReference>> powers) implements CustomPacketPayload {

		public static final Type<RevokeS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/revoke_powers"));
		public static final StreamCodec<ByteBuf, RevokeS2CPacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, RevokeS2CPacket::entityId,
			UPDATE_STREAM_CODEC, RevokeS2CPacket::powers,
			RevokeS2CPacket::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(Level level) {

			Entity holder = level.getEntity(entityId());

			if (holder == null) {
				NeoApoli.LOGGER.warn("Received packet for revoking {} power(s) to an unknown entity!", powers().size());
			}

			else {
				PowersImpl powers = (PowersImpl) Powers.getOrCreate(holder);
				powers().forEach((source, entries) -> entries.forEach((reference, invokeCallbacks) -> powers.revokePowerInternal(reference, source, invokeCallbacks)));
			}

		}

	}

	static {

		ServerEntityEvents.ENTITY_LOAD.register(ID, (entity, level) -> update(entity, entity instanceof Player));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(PowerManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, PowersImpl::update);

	}

}
