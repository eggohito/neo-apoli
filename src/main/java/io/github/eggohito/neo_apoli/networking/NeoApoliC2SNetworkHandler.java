package io.github.eggohito.neo_apoli.networking;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.networking.packet.c2s.RequestActionTagsC2SPacket;
import io.github.eggohito.neo_apoli.networking.packet.c2s.RequestPowerTagsC2SPacket;
import io.github.eggohito.neo_apoli.networking.packet.c2s.TriggerPowerImplsC2SPacket;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.misc.KeyBound;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class NeoApoliC2SNetworkHandler {

	public static void init() {

		ServerPlayConnectionEvents.INIT.register((handler, server) -> {
			ServerPlayNetworking.registerReceiver(handler, RequestPowerTagsC2SPacket.ID, NeoApoliC2SNetworkHandler::onPowerTagsRequest);
			ServerPlayNetworking.registerReceiver(handler, RequestActionTagsC2SPacket.ID, NeoApoliC2SNetworkHandler::onActionTagsRequest);
			ServerPlayNetworking.registerReceiver(handler, TriggerPowerImplsC2SPacket.ID, NeoApoliC2SNetworkHandler::onPowersTriggered);
		});

	}

	private static void onActionTagsRequest(RequestActionTagsC2SPacket payload, ServerPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received request for action tags from {}! Sending...", context.player().getName().getString());
		ActionManager.sendTagSyncPayload(context.player());
	}

	private static void onPowerTagsRequest(RequestPowerTagsC2SPacket payload, ServerPlayNetworking.Context context) {
		NeoApoli.LOGGER.info("Received request for power tags from {}! Sending...", context.player().getName().getString());
		PowerManager.sendTagSyncPayload(context.player());
	}

	private static void onPowersTriggered(TriggerPowerImplsC2SPacket payload, ServerPlayNetworking.Context networkContext) {

		ServerPlayerEntity player = networkContext.player();
		PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(player);

		for (var powerReference: payload.powerReferences()) {

			if (!PowerManager.contains(powerReference)) {
				NeoApoli.LOGGER.warn("Couldn't trigger unregistered {} from entity {}!", powerReference.asDisplayString(false), player.getName().getString());
			}

			else if (!powersComponent.hasInstance(powerReference)) {
				NeoApoli.LOGGER.warn("Couldn't trigger {} as entity {} doesn't have it!", powerReference.asDisplayString(false), player.getName().getString());
			}

			else {

				Power.Instance<?> powerInstance = powersComponent.getInstance(powerReference);
				Context context = powerInstance.createHolderContext();

				if (powerInstance instanceof KeyBound.Instance keyBoundInstance) {

					if (keyBoundInstance.shouldTrigger(context)) {
						keyBoundInstance.onPress(context);
					}

				}

				else {
					NeoApoli.LOGGER.warn("Couldn't trigger {} from entity {} as it doesn't have a keybound power implementation!", powerReference.asDisplayString(false), player.getName().getString());
				}

			}

		}

	}

}
