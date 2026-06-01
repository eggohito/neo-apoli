package io.github.eggohito.neo_apoli.client.condition.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientConditionManager extends ConditionManager {

	private static ClientboundUpdateConditionsPacket conditionsPacket;

	private ClientConditionManager(Void ignored) {

	}

	public static void init() {

	}

	static {


		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundUpdateConditionsPacket.TYPE, (payload, context) -> {
			conditionsPacket = payload;
			context.responseSender().sendPacket(ServerboundSyncAcknowledgedPacket.INSTANCE);
		});

		ClientPlayConnectionEvents.INIT.register(ID, (handler, client) -> {

			ClientPlayNetworking.registerReceiver(ClientboundUpdateConditionsPacket.TYPE, (payload, context) -> payload.handle(handler.registryAccess()));

			if (conditionsPacket != null) {
				conditionsPacket.handle(handler.registryAccess());
			}

			conditionsPacket = null;

		});

		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> conditions = ImmutableMap.of());

	}

}
