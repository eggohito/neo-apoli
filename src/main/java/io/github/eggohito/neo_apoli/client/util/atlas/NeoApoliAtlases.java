package io.github.eggohito.neo_apoli.client.util.atlas;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.client.event.TextureAtlasRegistrationEvents;

public interface NeoApoliAtlases {

	AtlasId OVERLAY = AtlasId.of(NeoApoli.id("overlay"));

	static void registerAll() {
		TextureAtlasRegistrationEvents.SIMPLE.register(registrant -> registrant.accept(OVERLAY));
	}

}
