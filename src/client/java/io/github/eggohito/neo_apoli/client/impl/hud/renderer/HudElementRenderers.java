package io.github.eggohito.neo_apoli.client.impl.hud.renderer;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.client.api.hud.renderer.HudElementRenderer;
import io.github.eggohito.neo_apoli.client.event.HudElementRendererEvents;
import io.github.eggohito.neo_apoli.client.impl.hud.renderer.custom.NauseaOverlayHudElementRenderer;
import io.github.eggohito.neo_apoli.client.impl.hud.renderer.custom.ResourceBarHudElementRenderer;
import io.github.eggohito.neo_apoli.client.impl.hud.renderer.custom.TextureOverlayHudElementRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;

public final class HudElementRenderers {

	public static void registerAll() {

		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer
			.attachLayerBefore(IdentifiedLayer.SLEEP, HudElementLayer.BELOW_HUD)
			.attachLayerAfter(IdentifiedLayer.SLEEP, HudElementLayer.ABOVE_HUD)
		);

		registerInternal("overlay/nausea", NauseaOverlayHudElementRenderer.INSTANCE);
		registerInternal("overlay/texture", TextureOverlayHudElementRenderer.INSTANCE);
		registerInternal("resource_bar", ResourceBarHudElementRenderer.INSTANCE);

	}

	private static <R extends HudElementRenderer> void registerInternal(String path, R renderer) {
		HudElementRendererEvents.RENDER.register(NeoApoli.id(path), renderer);
	}

}
