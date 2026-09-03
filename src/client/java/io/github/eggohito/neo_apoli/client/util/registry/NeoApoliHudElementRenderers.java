package io.github.eggohito.neo_apoli.client.util.registry;

import io.github.eggohito.neo_apoli.client.hud.HudElementHelper;
import io.github.eggohito.neo_apoli.client.hud.renderer.custom.NauseaOverlayHudElementRenderer;
import io.github.eggohito.neo_apoli.client.hud.renderer.custom.ResourceBarHudElementRenderer;
import io.github.eggohito.neo_apoli.client.hud.renderer.custom.TextureOverlayHudElementRenderer;
import io.github.eggohito.neo_apoli.registry.NeoApoliHudElementTypes;

public final class NeoApoliHudElementRenderers {

	public static void registerAll() {
		HudElementHelper.registerRenderer(NeoApoliHudElementTypes.NAUSEA_OVERLAY, NauseaOverlayHudElementRenderer.INSTANCE);
		HudElementHelper.registerRenderer(NeoApoliHudElementTypes.RESOURCE_BAR, ResourceBarHudElementRenderer.INSTANCE);
		HudElementHelper.registerRenderer(NeoApoliHudElementTypes.TEXTURE_OVERLAY, TextureOverlayHudElementRenderer.INSTANCE);
	}

}
