package io.github.eggohito.neo_apoli.network;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.duck.CommandStorageHolder;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.impl.power.PowersImpl;
import io.github.eggohito.neo_apoli.network.packet.s2c.*;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class NeoApoliS2CNetworkHandler {

	public static void init() {

		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(DismountEntityS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onEntityDismounted);
			ClientPlayNetworking.registerReceiver(MountEntityS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onEntityMounted);
			ClientPlayNetworking.registerReceiver(ActionManager.SynchronizeS2CPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(ActionManager.SynchronizeTagsS2CPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(ConditionManager.SynchronizeConditionsS2CPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(SynchronizeCommandStorageS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onDataCommandStorageSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizePowerDataS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onPowerDataSynchronized);
			ClientPlayNetworking.registerReceiver(SynchronizePowerRecipeDisplaysS2CPacket.TYPE, NeoApoliS2CNetworkHandler::onPowerRecipeDisplaysSynchronized);
			ClientPlayNetworking.registerReceiver(PowerManager.SynchronizeS2CPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(PowerManager.SynchronizeTagsS2CPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(PowersImpl.GrantS2CPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(PowersImpl.RevokeS2CPacket.TYPE,  (payload, context) -> payload.handle(context.player().level()));
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

	private static void onDataCommandStorageSynchronized(SynchronizeCommandStorageS2CPacket payload, ClientPlayNetworking.Context context) {
		((CommandStorageHolder) context.client()).neo_apoli$setStorage(payload.id(), payload.nbt());
	}

	private static void onPowerDataSynchronized(SynchronizePowerDataS2CPacket payload, ClientPlayNetworking.Context context) {

		Level level = context.player().level();
		Entity entity = level.getEntity(payload.entityId());

		payload.powersAndData().forEach((reference, data) -> {

			if (!PowerManager.contains(reference)) {
				NeoApoli.LOGGER.warn("Couldn't sync data of unregistered {}!", reference.asDisplayString(false));
			}

			else if (entity == null) {
				NeoApoli.LOGGER.warn("Couldn't sync data of {} to non-existent entity!", reference.asDisplayString(false));
			}

			else {

				Powers powers = Powers.getOrCreate(entity);
				RegistryOps<Tag> nbtOps = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);

				if (powers.hasInstance(reference)) {

					Power.Instance<?> instance = powers.getInstance(reference);
					Tag nbtData = data.convert(nbtOps).getValue();

					if (instance.decodeData(nbtOps, nbtData) instanceof DataResult.Error<Unit> error) {
						NeoApoli.LOGGER.warn("Couldn't decode data of {} to be synced to entity {}: {}", reference.asDisplayString(false), entity.getName().getString(), error.message());
					}

				}

				else {
					NeoApoli.LOGGER.warn("Couldn't sync data of {} to entity {} as it wasn't granted!", reference.asDisplayString(false), entity.getName().getString());
				}

			}

		});

	}

	private static void onPowerRecipeDisplaysSynchronized(SynchronizePowerRecipeDisplaysS2CPacket payload, ClientPlayNetworking.Context context) {
		((PowerRecipeDisplayHolder) context.client()).neo_apoli$setPowerIdsByIndex(payload.displays());
	}

	private static String getNameAndUuid(Entity entity) {
		return entity.getName().getString() + (entity instanceof Player ? "" : " (UUID: " + entity.getStringUUID() + ")");
	}

}
