package io.github.eggohito.neo_apoli.networking;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.networking.packet.c2s.RequestActionTagsC2SPacket;
import io.github.eggohito.neo_apoli.networking.packet.c2s.RequestPowerTagsC2SPacket;
import io.github.eggohito.neo_apoli.networking.packet.s2c.*;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class NeoApoliS2CNetworkHandler {

	public static void init() {

		ClientPlayConnectionEvents.INIT.register((clientPlayNetworkHandler, minecraftClient) -> {
			ClientPlayNetworking.registerReceiver(DismountEntityS2CPacket.ID, NeoApoliS2CNetworkHandler::onEntityDismounted);
			ClientPlayNetworking.registerReceiver(MountEntityS2CPacket.ID, NeoApoliS2CNetworkHandler::onEntityMounted);
			ClientPlayNetworking.registerReceiver(SynchronizePowersS2CPacket.ID, NeoApoliS2CNetworkHandler::onPowersSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizePowerTagsS2CPacket.ID, NeoApoliS2CNetworkHandler::onPowerTagsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeConditionsS2CPacket.ID, NeoApoliS2CNetworkHandler::onConditionsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeActionsS2CPacket.ID, NeoApoliS2CNetworkHandler::onActionsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeActionTagsS2CPacket.ID, NeoApoliS2CNetworkHandler::onActionTagsSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeDataCommandStorageS2CPacket.ID, NeoApoliS2CNetworkHandler::onDataCommandStorageSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizeEntityTypeTagCacheS2CPacket.ID, ModifyEntityTypeTagPower::receiveCache);
		});

	}

	private static void onEntityDismounted(DismountEntityS2CPacket payload, ClientPlayNetworking.Context context) {

		World world = context.player().getWorld();
		Entity passenger = world.getEntityById(payload.passengerId());

		if (passenger == null) {
			NeoApoli.LOGGER.warn("Received packet for dismounting unknown passenger!");
		}

		else {
			passenger.dismountVehicle();
		}

	}

	private static void onEntityMounted(MountEntityS2CPacket payload, ClientPlayNetworking.Context context) {

		ClientPlayerEntity clientPlayer = context.player();
		World world = clientPlayer.getWorld();

		Entity actor = world.getEntityById(payload.passengerId());
		Entity target = world.getEntityById(payload.vehicleId());

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

	private static String getNameAndUuid(Entity entity) {
		return entity.getName().getString() + (entity instanceof PlayerEntity ? "" : " (UUID: " + entity.getUuidAsString() + ")");
	}

	private static void onDataCommandStorageSynchronized(SynchronizeDataCommandStorageS2CPacket payload, ClientPlayNetworking.Context context) {
		((DataCommandStorageHolder) context.client()).neo_apoli$set(payload.id(), payload.nbt());
	}

	private static void onActionTagsSynchronized(SynchronizeActionTagsS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} action tag(s) from server!", payload.actionTags().size());
		ActionManager.receiveSyncTagPayload(payload, context);
	}

	private static void onActionsSynchronized(SynchronizeActionsS2CPacket payload, ClientPlayNetworking.Context context) {

		NeoApoli.LOGGER.info("Received {} action(s) from server!", payload.actions().size());
		ActionManager.receiveSyncPayload(payload, context);

		NeoApoli.LOGGER.info("Requesting action tags from server...");
		context.responseSender().sendPacket(new RequestActionTagsC2SPacket());

	}

	private static void onConditionsSynchronized(SynchronizeConditionsS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} condition(s) from server!", payload.conditions().size());
		ConditionManager.receiveSyncPayload(payload);
	}

	private static void onPowerTagsSynchronized(SynchronizePowerTagsS2CPacket payload, ClientPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received {} power tag(s) from server!", payload.powerTags().size());
		PowerManager.receiveSyncTagPayload(payload, context);
	}

	private static void onPowersSynchronized(SynchronizePowersS2CPacket payload, ClientPlayNetworking.Context context) {

		NeoApoli.LOGGER.info("Received {} power(s) from server!", payload.powers().size());
		PowerManager.receiveSyncPayload(payload, context);

		NeoApoli.LOGGER.info("Requesting power tags from server...");
		context.responseSender().sendPacket(new RequestPowerTagsC2SPacket());

	}

}
