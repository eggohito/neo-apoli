package io.github.eggohito.neo_apoli.client.action.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientActionManager extends ActionManager {

	private static ClientboundUpdateActionsPacket actionsPacket;
	private static ClientboundUpdateTagsPacket tagsPacket;

	private ClientActionManager(Void ignored) {

	}

	public static void init() {

	}

	static {

		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundSyncInitiatedPacket.TYPE, (payload, context) -> payload.handle(context.responseSender()));
		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundUpdateActionsPacket.TYPE, (payload, context) -> actionsPacket = payload);
		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundUpdateTagsPacket.TYPE, (payload, context) -> tagsPacket = payload);

		ClientPlayConnectionEvents.INIT.register(ID, (handler, client) -> {

			ClientPlayNetworking.registerReceiver(ClientboundUpdateActionsPacket.TYPE, (payload, context) -> payload.handle(handler.registryAccess()));
			ClientPlayNetworking.registerReceiver(ClientboundUpdateTagsPacket.TYPE, (payload, context) -> payload.handle());

			if (actionsPacket != null) {
				actionsPacket.handle(handler.registryAccess());
			}

			if (tagsPacket != null) {
				tagsPacket.handle();
			}

			actionsPacket = null;
			tagsPacket = null;

		});

		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> {
			actions = ImmutableMap.of();
			tags = ImmutableMap.of();
		});

	}

}
