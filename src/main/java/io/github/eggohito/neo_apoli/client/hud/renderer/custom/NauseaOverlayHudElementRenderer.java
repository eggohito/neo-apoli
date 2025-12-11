package io.github.eggohito.neo_apoli.client.hud.renderer.custom;

import io.github.eggohito.neo_apoli.client.hud.renderer.OverlayHudElementRenderer;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.custom.NauseaOverlayHudElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

//	TODO: Move away from using the GUI atlas and make our own specifically for overlays
public record NauseaOverlayHudElementRenderer() implements OverlayHudElementRenderer {

	@Override
	public void start(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof NauseaOverlayHudElement nauseaOverlay) || !this.visibleBasedOnPerspective(context, nauseaOverlay)) {
			return;
		}

		int scaledWidth = graphics.guiWidth();
		int scaledHeight = graphics.guiHeight();

		Context colorContext = context.makeChild(".color");
		int color = nauseaOverlay.color().getValue(colorContext);

		float alphaAsStrength = ARGB.alphaFloat(color);
		float red = ARGB.redFloat(color) * alphaAsStrength;
		float green = ARGB.greenFloat(color) * alphaAsStrength;
		float blue = ARGB.blueFloat(color) * alphaAsStrength;

		float stretch = Mth.lerp(alphaAsStrength, 2.0F, 1.0F);

		graphics.pose().pushPose();

		graphics.pose().translate(scaledWidth / 2.0F, scaledHeight / 2.0F, 0.0F);
		graphics.pose().scale(stretch, stretch, stretch);
		graphics.pose().translate(-scaledWidth / 2.0F, -scaledHeight / 2.0F, 0.0F);

		graphics.blitSprite(id -> RenderType.guiNauseaOverlay(), nauseaOverlay.sprite(), 0, 0, scaledWidth, scaledHeight, ARGB.colorFromFloat(1.0F, red, green, blue));

		graphics.pose().popPose();

	}

	@Override
	public void end(GuiGraphics graphics, DeltaTracker delta) {

	}

}
