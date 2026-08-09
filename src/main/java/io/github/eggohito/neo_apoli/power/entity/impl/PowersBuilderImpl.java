package io.github.eggohito.neo_apoli.power.entity.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.attachment.entity.PowersAttachment;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundPowerDataUpdatePacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import io.github.eggohito.neo_apoli.power.entity.PowersBuilder;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import io.github.eggohito.neo_apoli.registry.attachment.NeoApoliEntityAttachments;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.Consumers;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public final class PowersBuilderImpl extends AbstractPowers implements PowersBuilder {

	private static final StreamCodec<ByteBuf, Object2BooleanMap<PowerIdentifier>> ID_AND_CALLBACK_STREAM_CODEC = ByteBufCodecs.map(Object2BooleanOpenHashMap::new, PowerIdentifier.STREAM_CODEC, ByteBufCodecs.BOOL);
	private static final StreamCodec<ByteBuf, Object2ObjectMap<ResourceLocation, Object2BooleanMap<PowerIdentifier>>> UPDATE_STREAM_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, ID_AND_CALLBACK_STREAM_CODEC);

	private final Object2ObjectMap<ResourceLocation, Object2BooleanMap<PowerIdentifier>> grantedPowers = new Object2ObjectLinkedOpenHashMap<>();
	private final Object2ObjectMap<ResourceLocation, Object2BooleanMap<PowerIdentifier>> revokedPowers = new Object2ObjectLinkedOpenHashMap<>();

	PowersBuilderImpl(Entity holder, PowersAttachment attachment) {
		super(holder, new Object2ObjectLinkedOpenHashMap<>(attachment.instances()), LinkedHashMultimap.create(attachment.sources()));
	}

	@Override
	public boolean grant(PowerIdentifier id, ResourceLocation source, boolean invokeCallbacks) {
		return !holder.level().isClientSide()
			&& this.grantInternal(id, source, invokeCallbacks);
	}

	@Override
	public boolean revoke(PowerIdentifier id, ResourceLocation source, boolean invokeCallbacks) {
		return !holder.level().isClientSide()
			&& this.revokeInternal(id, source, invokeCallbacks);
	}

	@Override
	public void build() {

		if (holder.level().isClientSide()) {
			return;
		}

		boolean revoked = !this.revokedPowers.isEmpty();
		boolean granted = !this.grantedPowers.isEmpty();

		if (revoked || granted) {

			if (revoked) {
				MiscUtil.broadcastCustomToAll(holder, () -> new ClientboundRevokePowersPacket(holder.getId(), new Object2ObjectLinkedOpenHashMap<>(this.revokedPowers)));
			}

			if (granted) {
				MiscUtil.broadcastCustomToAll(holder, () -> new ClientboundGrantPowersPacket(holder.getId(), new Object2ObjectLinkedOpenHashMap<>(this.grantedPowers)));
			}

			this.holder.setAttached(NeoApoliEntityAttachments.POWERS, new PowersAttachment(instances, sources));

		}

		this.revokedPowers.clear();
		this.grantedPowers.clear();

	}

	private boolean grantInternal(PowerIdentifier id, ResourceLocation source, boolean invokeCallbacks) {

		List<Power.Instance<?>> addedPowers = new ObjectArrayList<>();
		List<Power.Instance<?>> grantedPowers = new ObjectArrayList<>();

		boolean granted = this.grantRecursively(id, source, addedPowers::add, grantedPowers::add, invokeCallbacks);

		addedPowers.forEach(instance -> instance.onAdded(holder));
		grantedPowers.forEach(instance -> instance.onGranted(holder));

		return granted;

	}

	private boolean grantRecursively(PowerIdentifier id, ResourceLocation source, Consumer<Power.Instance<?>> onAdded, Consumer<Power.Instance<?>> onGranted, boolean invokeCallbacks) {

		if (!PowerManager.getInstance().contains(id)) {
			return false;
		}

		Set<ResourceLocation> sources = PowersBuilderImpl.this.sources.get(id);
		boolean firstTimeGranting = !instances.containsKey(id);

		if (!sources.add(source)) {
			return false;
		}

		Power power = PowerManager.getInstance().get(id).value();
		Power.Instance<?> instance = instances.computeIfAbsent(id, k -> power.createInstance());

		if (power instanceof MultiplePower(ImmutableSet<PowerHolder<?>> subPowers)) {

			for (var subPower : subPowers) {
				this.grantRecursively(subPower.id(), source, onAdded, onGranted, invokeCallbacks);
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
				.put(id, invokeCallbacks);
		}

		return true;

	}

	private boolean revokeInternal(PowerIdentifier id, ResourceLocation source, boolean invokeCallbacks) {

		List<PowerIdentifier> revokedPowers = new ObjectArrayList<>();
		boolean result = this.revokeRecursively(id, source, revokedPowers::add, invokeCallbacks);

		instances.keySet().removeIf(revokedPowers::contains);
		sources.keySet().removeIf(revokedPowers::contains);

		return result;

	}

	private boolean revokeRecursively(PowerIdentifier id, ResourceLocation source, Consumer<PowerIdentifier> onRevoked, boolean invokeCallbacks) {

		if (!sources.remove(id, source) || !instances.containsKey(id)) {
			return false;
		}

		Power.Instance<?> instance = instances.get(id);
		boolean revoked = sources.get(id).isEmpty();

		if (instance.power() instanceof MultiplePower(ImmutableSet<PowerHolder<?>> subPowers)) {

			for (var subPower : subPowers) {
				this.revokeRecursively(subPower.id(), source, onRevoked, invokeCallbacks);
			}

		}

		if (revoked) {

			onRevoked.accept(id);

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
				.put(id, invokeCallbacks);
		}

		return true;

	}


	public static PowersBuilder of(@NotNull Entity holder) {
		return new PowersBuilderImpl(holder, holder.getAttachedOrCreate(NeoApoliEntityAttachments.POWERS));
	}


	private static void onReload(ServerPlayer player, boolean joined) {

		if (joined || !Powers.has(player)) {
			return;
		}

		PowersBuilder powers = of(player);
		RegistryOps<Tag> ops = player.registryAccess().createSerializationContext(NbtOps.INSTANCE);

		Map<PowerIdentifier, Tag> pendingDataSync = new Object2ObjectOpenHashMap<>();
		Map<PowerIdentifier, Power.Type<?>> oldTypes = new Object2ObjectOpenHashMap<>();

		//  Revoke all unregistered powers, and cache the old data of those that are on the entity
		for (var reference : powers.getAllIds()) {

			if (!PowerManager.getInstance().contains(reference)) {

				for (var source : powers.getSources(reference)) {
					powers.revokeWithCallback(reference, source);
				}

				NeoApoli.LOGGER.warn("Removed unregistered {} from entity {}!", reference.asDisplayString(false), player.getName().getString());

			}

			else {

				Power.Instance<?> oldInstance = powers.getInstance(reference);
				Power.Type<?> oldType = oldInstance.power().getType();

				oldTypes.put(reference, oldType);
				MiscUtil.handleResult(
					oldInstance.encodeData(ops),
					tag -> pendingDataSync.put(reference, tag),
					warning -> NeoApoli.LOGGER.warn("Couldn't fully encode old data of {} from entity {} during the update process (proceeding with partial result): {}", reference.asDisplayString(false), player.getName().getString(), warning),
					error -> NeoApoli.LOGGER.warn("Couldn't encode old data of {} from entity {} during the update process (skipping): {}", reference.asDisplayString(false), player.getName().getString(), error)
				);

			}

		}

		//  Re-grant all the existing powers and restore its old data
		for (var reference : powers.getAllIds()) {

			for (var source : powers.getSources(reference)) {
				powers.revokeWithoutCallback(reference, source);
				powers.grantWithoutCallback(reference, source);
			}

			if (pendingDataSync.containsKey(reference)) {

				Tag oldData = pendingDataSync.get(reference);
				Power.Instance<?> newInstance = powers.getInstance(reference);

				if (Objects.equals(oldTypes.get(reference), newInstance.power().getType())) {
					MiscUtil.handleResult(
						newInstance.decodeData(ops, oldData),
						Consumers.nop(),
						warning -> NeoApoli.LOGGER.warn("Couldn't fully decode old data of {} from entity {} during the update process (proceeding with partial result): {}", reference.asDisplayString(false), player.getName().getString(), warning),
						error -> NeoApoli.LOGGER.warn("Couldn't decode old data of {} from entity {} during the update process (skipping): {}", reference.asDisplayString(false), player.getName().getString(), error)
					);
				}

				else {
					NeoApoli.LOGGER.warn("Couldn't transfer old data of {} from entity {}, as it's now using a different power type!", reference.asDisplayString(false), player.getName().getString());
				}

			}

		}

		powers.build();

		if (!pendingDataSync.isEmpty()) {
			MiscUtil.broadcastCustomToAll(player, () -> ClientboundPowerDataUpdatePacket.bulk(player.getId(), ops, pendingDataSync));
		}

	}


	public record ClientboundGrantPowersPacket(int entityId, Object2ObjectMap<ResourceLocation, Object2BooleanMap<PowerIdentifier>> powers) implements CustomPacketPayload {

		public static final Type<ClientboundGrantPowersPacket> TYPE = new Type<>(NeoApoli.id("clientbound/grant_powers"));
		public static final StreamCodec<ByteBuf, ClientboundGrantPowersPacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ClientboundGrantPowersPacket::entityId,
			UPDATE_STREAM_CODEC, ClientboundGrantPowersPacket::powers,
			ClientboundGrantPowersPacket::new
		);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(Level level) {

			Entity holder = level.getEntity(entityId());

			if (holder == null) {
				NeoApoli.LOGGER.warn("Received packet for granting {} power(s) to an unknown entity with ID {}!", powers.size(), entityId());
			}

			else {

				PowersBuilderImpl builderImpl = (PowersBuilderImpl) of(holder);

				powers().forEach((source, entries) ->
					entries.forEach((id, invokeCallbacks) ->
						builderImpl.grantInternal(id, source, invokeCallbacks)));

			}

		}

	}

	public record ClientboundRevokePowersPacket(int entityId, Object2ObjectMap<ResourceLocation, Object2BooleanMap<PowerIdentifier>> powers) implements CustomPacketPayload {

		public static final Type<ClientboundRevokePowersPacket> TYPE = new Type<>(NeoApoli.id("clientbound/revoke_powers"));
		public static final StreamCodec<ByteBuf, ClientboundRevokePowersPacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ClientboundRevokePowersPacket::entityId,
			UPDATE_STREAM_CODEC, ClientboundRevokePowersPacket::powers,
			ClientboundRevokePowersPacket::new
		);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(Level level) {

			Entity holder = level.getEntity(entityId());

			if (holder == null) {
				NeoApoli.LOGGER.warn("Received packet for revoking {} power(s) to an unknown entity with ID {}!", powers.size(), entityId());
			}

			else {

				PowersBuilderImpl builderImpl = (PowersBuilderImpl) of(holder);

				powers().forEach((source, entries) ->
					entries.forEach((id, invokeCallbacks) ->
						builderImpl.revokeInternal(id, source, invokeCallbacks)));

			}

		}

	}

	static {
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(PowerManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, PowersBuilderImpl::onReload);
	}

}
