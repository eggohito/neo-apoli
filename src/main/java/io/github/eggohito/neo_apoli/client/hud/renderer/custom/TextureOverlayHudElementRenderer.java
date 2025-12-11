package io.github.eggohito.neo_apoli.client.hud.renderer.custom;

import io.github.eggohito.neo_apoli.client.hud.renderer.OverlayHudElementRenderer;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.custom.TextureOverlayHudElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

//	TODO: Move away from using the GUI atlas and make our own specifically for overlays
public record TextureOverlayHudElementRenderer() implements OverlayHudElementRenderer {

	@Override
	public void start(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof TextureOverlayHudElement textureOverlay) || !this.visibleBasedOnPerspective(context, textureOverlay)) {
			return;
		}

		int scaledWidth = graphics.guiWidth();
		int scaledHeight = graphics.guiHeight();

		Context colorContext = context.makeChild(".color");
		int color = textureOverlay.color().getValue(colorContext);

		graphics.blitSprite(RenderType::guiTexturedOverlay, textureOverlay.sprite(), 0, 0, scaledWidth, scaledHeight, color);

	}

	@Override
	public void end(GuiGraphics graphics, DeltaTracker delta) {

	}

}
