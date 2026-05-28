package io.github.eggohito.neo_apoli.client.impl.log;

import io.github.eggohito.neo_apoli.impl.log.NeoApoliLoggerImpl;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundLogsClearPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NeoApoliClientLoggerImpl extends NeoApoliLoggerImpl {

	public static void init() {

		ClientPlayConnectionEvents.INIT.register((listener, client) ->
			ClientPlayNetworking.registerReceiver(ClientboundLogsClearPacket.TYPE, (payload, context) -> CACHE.clear())
		);

		ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> CACHE.clear());

	}

}
