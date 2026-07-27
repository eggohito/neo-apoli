package io.github.eggohito.neo_apoli.client.power.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundUpdatePowersPacket;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import io.github.eggohito.neo_apoli.power.manager.ServerPowerManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ClientPowerManager extends ServerPowerManager {

	private void receive(ClientboundUpdatePowersPacket payload) {
		this.contents = ImmutableMap.copyOf(payload.powers());
		this.tags = ImmutableMap.copyOf(payload.tags());
	}

	private void clear() {
		this.contents = ImmutableMap.of();
		this.tags = ImmutableMap.of();
	}

	public static void init() {

		if (!(INSTANCE instanceof ClientPowerManager clientPowerManager)) {
			throw new IllegalStateException("Expected '" + ClientPowerManager.class.getName() + "', got '" + INSTANCE.getClass().getName() + "'");
		}

		ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdatePowersPacket.TYPE, (payload, context) -> clientPowerManager.receive(payload));
		ClientPlayConnectionEvents.DISCONNECT.register(PowerManager.ID, (handler, client) -> clientPowerManager.clear());

	}

}
