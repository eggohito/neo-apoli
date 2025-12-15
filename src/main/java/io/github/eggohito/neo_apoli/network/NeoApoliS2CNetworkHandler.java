package io.github.eggohito.neo_apoli.network;

import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.duck.CommandStorageHolder;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.network.packet.c2s.RequestActionTagsC2SPacket;
import io.github.eggohito.neo_apoli.network.packet.c2s.RequestPowerTagsC2SPacket;
import io.github.eggohito.neo_apoli.network.packet.s2c.*;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower;
import io.github.eggohito.neo_apoli.util.NeoApoliLogger;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class NeoApoliS2CNetworkHandler {

	public static void init() {

		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(ClearLogsS2CPacket.TYPE, NeoApoliLogger::onReloadClientBound);
			ClientPlayNetworking.registerReceiver(DismountEntityS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onEntityDismounted);
			ClientPlayNetworking.registerReceiver(MountEntityS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onEntityMounted);
			ClientPlayNetworking.registerReceiver(SynchronizeActionsS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onActionsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeActionTagsS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onActionTagsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeConditionsS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onConditionsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeCommandStorageS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onDataCommandStorageSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeEntityTypeTagCacheS2CPacket.TYPE, ModifyEntityTypeTagPower::receiveCache);
			ClientPlayNetworking.registerReceiver(SynchronizePowerDataS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onPowerDataSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizePowerRecipeDisplaysS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onPowerRecipeDisplaysSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizePowersS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onPowersSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizePowerTagsS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onPowerTagsSynchronized);
		});

	}

	private static void onEntityDismounted(DismountEntityS2CPacket payload, ClientPlayNetworking.Context context) {

		Level world = context.player().level();
		Entity passenger = world.getEntity(payload.passengerId());

		if (passenger == null) {
			NeoApoli.LOGGER.warn("Received packet for dismounting unknown passenger!");
		}

		else {
			passenger.removeVehicle();
		}

	}

	private static void onEntityMounted(MountEntityS2CPacket payload, ClientPlayNetworking.Context context) {

		LocalPlayer clientPlayer = context.player();
		Level world = clientPlayer.level();

		Entity actor = world.getEntity(payload.passengerId());
		Entity target = world.getEntity(payload.vehicleId());

		if (target == null) {
			NeoApoli.LOGGER.warn("Received packet for passenger for unknown entity!");
		}

		else if (actor == null) {
			NeoApoli.LOGGER.warn("Received packet for unknown passenger for entity {}!", getNameAndUuid(target));
		}

		else if (actor.startRiding(target, payload.force())) {
			NeoApoli.LOGGER.info("Entity {} started riding entity {}!", getNameAndUuid(actor), getNameAndUuid(target));
		}

		else {
			NeoApoli.LOGGER.warn("Entity {} failed to start riding entity {}!", getNameAndUuid(actor), getNameAndUuid(target));
		}

	}

	private static void onActionsSynchronized(SynchronizeActionsS2CPacket payload, ClientPlayNetworking.Context context) {

		NeoApoli.LOGGER.info("Received {} action(s) from server!", payload.actions().size());
		ActionManager.receiveSyncPayload(payload, context);

		NeoApoli.LOGGER.info("Requesting action tags from server...");
		context.responseSender().sendPacket(new RequestActionTagsC2SPacket());

	}

	private static void onActionTagsSynchronized(SynchronizeActionTagsS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} action tag(s) from server!", payload.tags().size());
		ActionManager.receiveTagSyncPayload(payload, context);
	}

	private static void onConditionsSynchronized(SynchronizeConditionsS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} condition(s) from server!", payload.conditions().size());
		ConditionManager.receiveSyncPayload(payload, context);
	}

	private static void onDataCommandStorageSynchronized(SynchronizeCommandStorageS2CPacket payload, ClientPlayNetworking.Context context) {
		((CommandStorageHolder) context.client()).neo_apoli$set(payload.id(), payload.nbt());
	}

	private static void onPowerDataSynchronized(SynchronizePowerDataS2CPacket payload, ClientPlayNetworking.Context context) {

		Entity entity = context.player().level().getEntity(payload.entityId());
		PowerReference powerReference = payload.powerReference();

		if (!PowerManager.contains(powerReference)) {
			NeoApoli.LOGGER.warn("Couldn't sync data of unregistered {}!", powerReference.asDisplayString(false));
		}

		else if (entity == null) {
			NeoApoli.LOGGER.warn("Couldn't sync data of {} to non-existent entity!", powerReference.asDisplayString(false));
		}

		else {

			PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(entity);
			RegistryOps<Tag> nbtOps = entity.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);

			if (powersComponent.hasInstance(powerReference)) {

				Power.Instance<?> instance = powersComponent.getInstance(powerReference);
				Tag nbtData = payload.data().convert(nbtOps).getValue();

				if (instance.decodeData(nbtOps, nbtData) instanceof DataResult.Error<Unit> error) {
					NeoApoli.LOGGER.warn("Couldn't decode data of {} to be synced to entity {}: {}", powerReference.asDisplayString(false), entity.getName().getString(), error.message());
				}

			}

			else {
				NeoApoli.LOGGER.warn("Couldn't sync data of {} to entity {} as it wasn't granted!", powerReference.asDisplayString(false), entity.getName().getString());
			}

		}

	}

	private static void onPowerRecipeDisplaysSynchronized(SynchronizePowerRecipeDisplaysS2CPacket payload, ClientPlayNetworking.Context context) {
		((PowerRecipeDisplayHolder) context.client()).neo_apoli$setReferencesByDisplayEntry(payload.displays());
	}

	private static void onPowersSynchronized(SynchronizePowersS2CPacket payload, ClientPlayNetworking.Context context) {

		NeoApoli.LOGGER.info("Received {} power(s) from server!", payload.powers().size());
		PowerManager.receiveSyncPayload(payload, context);

		NeoApoli.LOGGER.info("Requesting power tags from server...");
		context.responseSender().sendPacket(new RequestPowerTagsC2SPacket());

	}

	private static void onPowerTagsSynchronized(SynchronizePowerTagsS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} power tag(s) from server!", payload.powerTags().size());
		PowerManager.receiveSyncTagPayload(payload, context);
	}

	private static String getNameAndUuid(Entity entity) {
		return entity.getName().getString() + (entity instanceof Player ? "" : " (UUID: " + entity.getStringUUID() + ")");
	}

}
