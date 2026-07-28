package io.github.eggohito.neo_apoli.client.condition.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.condition.manager.ServerConditionManager;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundUpdateConditionsPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ClientConditionManager extends ServerConditionManager {

	@Override
	public void init() {

		super.init();

		ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateConditionsPacket.TYPE, (payload, context) -> this.receive(payload));
		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> this.clear());

	}

	private void receive(ClientboundUpdateConditionsPacket payload) {
		this.contents = ImmutableMap.copyOf(payload.conditions());
	}

	private void clear() {
		this.contents = ImmutableMap.of();
	}

}
