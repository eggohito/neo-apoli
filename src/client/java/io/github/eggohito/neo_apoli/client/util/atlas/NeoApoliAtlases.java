package io.github.eggohito.neo_apoli.client.util.atlas;

import io.github.eggohito.neo_apoli.client.event.TextureAtlasRegistrationEvents;
import io.github.eggohito.neo_apoli.hud.element.OverlayHudElement;

public interface NeoApoliAtlases {

	AtlasId OVERLAY = AtlasId.of(OverlayHudElement.ATLAS_SHEET, OverlayHudElement.ATLAS_NAME);

	static void registerAll() {
		TextureAtlasRegistrationEvents.SIMPLE.register(registrant -> registrant.accept(OVERLAY));
	}

}
