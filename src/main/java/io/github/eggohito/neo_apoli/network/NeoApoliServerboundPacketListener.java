package io.github.eggohito.neo_apoli.network;

import io.github.eggohito.neo_apoli.impl.key.KeyStateManagerImpl;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NeoApoliServerboundPacketListener {

	public static void init() {

		ServerPlayConnectionEvents.INIT.register((listener, server) ->
			ServerPlayNetworking.registerReceiver(listener, KeyStateManagerImpl.ServerboundKeyStatesUpdatePacket.TYPE, (payload, context) -> payload.handle(context.player()))
		);

	}

}
