package io.github.eggohito.neo_apoli;

import io.github.eggohito.neo_apoli.networking.packet.NeoApoliS2CNetworkHandler;
import net.fabricmc.api.ClientModInitializer;

public class NeoApoliClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		NeoApoliS2CNetworkHandler.init();
	}

}
