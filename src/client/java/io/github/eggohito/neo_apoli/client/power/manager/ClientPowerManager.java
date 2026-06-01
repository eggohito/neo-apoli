package io.github.eggohito.neo_apoli.client.power.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientPowerManager extends PowerManager {

	private static ClientboundUpdatePowersPacket powersPacket;
	private static ClientboundUpdateTagsPacket tagsPacket;

	private ClientPowerManager() {

	}

	public static void init() {

	}

	static {

		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundSyncInitiatedPacket.TYPE, (payload, context) -> payload.handle(context.responseSender()));
		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundUpdatePowersPacket.TYPE, (payload, context) -> powersPacket = payload);
		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundUpdateTagsPacket.TYPE, (payload, context) -> tagsPacket = payload);

		ClientPlayConnectionEvents.INIT.register(ID, (handler, client) -> {

			ClientPlayNetworking.registerReceiver(ClientboundUpdatePowersPacket.TYPE, (payload, context) -> payload.handle(handler.registryAccess()));
			ClientPlayNetworking.registerReceiver(ClientboundUpdateTagsPacket.TYPE, (payload, context) -> payload.handle());

			if (powersPacket != null) {
				powersPacket.handle(handler.registryAccess());
			}

			if (tagsPacket != null) {
				tagsPacket.handle();
			}

			powersPacket = null;
			tagsPacket = null;

		});

		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> {
			powers = ImmutableMap.of();
			tags = ImmutableMap.of();
		});

	}

}
