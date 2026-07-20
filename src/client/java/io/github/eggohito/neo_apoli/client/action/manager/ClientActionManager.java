package io.github.eggohito.neo_apoli.client.action.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.action.manager.ServerActionManager;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundUpdateActionsPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ClientActionManager extends ServerActionManager {

	private void receive(ClientboundUpdateActionsPacket payload) {
		this.contents = ImmutableMap.copyOf(payload.actions());
		this.tags = ImmutableMap.copyOf(payload.tags());
	}

	private void clear() {
		this.contents = ImmutableMap.of();
		this.tags = ImmutableMap.of();
	}

	public static void init() {

		if (!(INSTANCE instanceof ClientActionManager clientActionManager)) {
			throw new IllegalStateException("Instantiated action manager doesn't match the client environment! (Is " + INSTANCE.getClass().getName() + ", must be " + ClientActionManager.class.getName() + ")");
		}

		ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateActionsPacket.TYPE, (payload, context) -> clientActionManager.receive(payload));
		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> clientActionManager.clear());

	}

}
