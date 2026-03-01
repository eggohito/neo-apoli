package io.github.eggohito.neo_apoli.client.impl.hud.renderer.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.client.api.hud.renderer.OverlayHudElementRenderer;
import io.github.eggohito.neo_apoli.client.util.NeoApoliRenderTypes;
import io.github.eggohito.neo_apoli.client.util.SpriteMaterial;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.custom.NauseaOverlayHudElement;
import io.github.eggohito.neo_apoli.util.Reporter;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.slf4j.event.Level;

public enum NauseaOverlayHudElementRenderer implements OverlayHudElementRenderer {

	INSTANCE;

	@Override
	public void render(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof NauseaOverlayHudElement nauseaOverlay) || !this.isVisibleInPerspective(context, nauseaOverlay)) {
			return;
		}

		Reporter reporter = context.reporter();
		SpriteMaterial spriteMaterial = new SpriteMaterial(nauseaOverlay.sprite());

		TextureAtlasSprite sprite = spriteMaterial.spriteAsResult()
			.resultOrPartial(reporter::report)
			.orElse(null);

		if (sprite == null || reporter.hasErrors()) {

			reporter.getErrorsFlattened().ifPresent(error -> NeoApoli.logOnce(Level.ERROR, "Error trying to render overlay HUD element(s) due to error(s) " + error));

			return;

		}

		float scaledWidth = graphics.guiWidth();
		float scaledHeight = graphics.guiHeight();

		Context colorContext = context.forChild(".color");
		int color = nauseaOverlay.color().intValue(colorContext);

		float intensity = ARGB.alphaFloat(color);
		float red = ARGB.redFloat(color) * intensity;
		float green = ARGB.greenFloat(color) * intensity;
		float blue = ARGB.blueFloat(color) * intensity;

		float stretch = Mth.lerp(intensity, 2.0F, 1.0F);

		float width = scaledWidth * stretch;
		float height = scaledHeight * stretch;

		float x1 = (scaledWidth - width) / 2.0F;
		float x2 = x1 + width;

		float y1 = (scaledHeight - height) / 2.0F;
		float y2 = y1 + height;

		float minU = sprite.getU0();
		float maxU = sprite.getU1();

		float minV = sprite.getV0();
		float maxV = sprite.getV1();

		color = ARGB.colorFromFloat(1.0F, red, green, blue);

		Matrix4f matrices = graphics.pose().last().pose();
		VertexConsumer vertexBuffer = spriteMaterial.buffer(Minecraft.getInstance().renderBuffers().bufferSource(), NeoApoliRenderTypes.GUI_NAUSEA_OVERLAY);

		vertexBuffer.addVertex(matrices, x1, y1, 0.0F).setUv(minU, minV).setColor(color);
		vertexBuffer.addVertex(matrices, x1, y2, 0.0F).setUv(minU, maxV).setColor(color);
		vertexBuffer.addVertex(matrices, x2, y2, 0.0F).setUv(maxU, maxV).setColor(color);
		vertexBuffer.addVertex(matrices, x2, y1, 0.0F).setUv(maxU, minV).setColor(color);

		reporter.getErrorsFlattened().ifPresent(warn -> NeoApoli.logOnce(Level.WARN, "Found warnings when rendering overlay HUD element(s) " + warn));

	}

}
