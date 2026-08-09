package io.github.eggohito.neo_apoli.power.entity.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.LinkedHashMultimap;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.attachment.entity.PowersAttachment;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundPowerDataUpdatePacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.custom.MultiplePower;
import io.github.eggohito.neo_apoli.power.entity.MutablePowers;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import io.github.eggohito.neo_apoli.registry.attachment.NeoApoliEntityAttachments;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.function.Consumers;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public final class MutablePowersImpl extends AbstractPowers implements MutablePowers {

	private boolean changed = false;

	MutablePowersImpl(Entity holder, PowersAttachment attachment) {
		super(holder, new Object2ObjectLinkedOpenHashMap<>(attachment.instances()), LinkedHashMultimap.create(attachment.sources()));
	}

	@Override
	public boolean grant(PowerIdentifier id, ResourceLocation source) {
		return !holder.level().isClientSide()
			&& this.grantInternal(id, source);
	}

	@Override
	public boolean revoke(PowerIdentifier id, ResourceLocation source) {
		return !holder.level().isClientSide()
			&& this.revokeInternal(id, source);
	}

	@Override
	public void applyChanges() {

		if (!holder.level().isClientSide() && changed) {

			if (instances.isEmpty() || sources.isEmpty()) {
				this.holder.removeAttached(NeoApoliEntityAttachments.POWERS);
			}

			else {
				this.holder.setAttached(NeoApoliEntityAttachments.POWERS, new PowersAttachment(ImmutableMap.copyOf(this.instances), ImmutableSetMultimap.copyOf(this.sources)));
			}

		}

		this.changed = false;

	}

	private boolean grantInternal(PowerIdentifier id, ResourceLocation source) {

		if (!PowerManager.getInstance().contains(id) || !this.sources.put(id, source)) {
			return false;
		}

		Power power = PowerManager.getInstance().get(id).value();
		this.instances.computeIfAbsent(id, k -> power.createInstance());

		if (power instanceof MultiplePower(ImmutableSet<PowerHolder<?>> subPowers)) {

			for (var subPower : subPowers) {
				this.grantInternal(subPower.id(), source);
			}

		}

		return this.changed = true;

	}

	private boolean revokeInternal(PowerIdentifier id, ResourceLocation source) {

		if (!sources.remove(id, source) || !instances.containsKey(id)) {
			return false;
		}

		if (instances.get(id).power() instanceof MultiplePower(ImmutableSet<PowerHolder<?>> subPowers)) {

			for (var subPower : subPowers) {
				this.revokeInternal(subPower.id(), source);
			}

		}

		return this.changed = true;

	}


	public static MutablePowers of(@NotNull Entity holder) {
		return new MutablePowersImpl(holder, holder.getAttachedOrCreate(NeoApoliEntityAttachments.POWERS));
	}


	private static void onReload(ServerPlayer player, boolean joined) {

		if (joined || !Powers.has(player)) {
			return;
		}

		MutablePowers powers = of(player);
		RegistryOps<Tag> ops = player.registryAccess().createSerializationContext(NbtOps.INSTANCE);

		Map<PowerIdentifier, Tag> pendingDataSync = new Object2ObjectOpenHashMap<>();
		Map<PowerIdentifier, Power.Type<?>> oldTypes = new Object2ObjectOpenHashMap<>();

		//  Revoke all unregistered powers, and cache the old data of those that are on the entity
		for (var reference : powers.getAllIds()) {

			if (!PowerManager.getInstance().contains(reference)) {

				for (var source : powers.getSources(reference)) {
					powers.revoke(reference, source);
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
				powers.revoke(reference, source);
				powers.grant(reference, source);
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

		powers.applyChanges();

		if (!pendingDataSync.isEmpty()) {
			MiscUtil.broadcastCustomToAll(player, () -> ClientboundPowerDataUpdatePacket.bulk(player.getId(), ops, pendingDataSync));
		}

	}


	static {
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.addPhaseOrdering(PowerManager.ID, ID);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ID, MutablePowersImpl::onReload);
	}

}
