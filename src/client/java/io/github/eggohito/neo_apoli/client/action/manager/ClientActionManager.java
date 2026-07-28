package io.github.eggohito.neo_apoli.client.action.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.action.manager.ServerActionManager;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundUpdateActionsPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ClientActionManager extends ServerActionManager {

	@Override
	public void init() {

		super.init();

		ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateActionsPacket.TYPE, (payload, context) -> this.receive(payload));
		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> this.clear());

	}

	private void receive(ClientboundUpdateActionsPacket payload) {
		this.contents = ImmutableMap.copyOf(payload.actions());
		this.tags = ImmutableMap.copyOf(payload.tags());
	}

	private void clear() {
		this.contents = ImmutableMap.of();
		this.tags = ImmutableMap.of();
	}

}
