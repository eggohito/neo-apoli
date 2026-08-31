package io.github.eggohito.neo_apoli.client.util.registry;

import io.github.eggohito.neo_apoli.client.event.TextureAtlasRegistrationEvents;
import io.github.eggohito.neo_apoli.client.util.atlas.AtlasId;
import io.github.eggohito.neo_apoli.hud.element.OverlayHudElement;

public final class NeoApoliAtlases {

	public static final AtlasId OVERLAY = AtlasId.of(OverlayHudElement.ATLAS_SHEET, OverlayHudElement.ATLAS_NAME);

	public static void registerAll() {
		TextureAtlasRegistrationEvents.SIMPLE.register(registrant -> registrant.accept(OVERLAY));
	}

}
