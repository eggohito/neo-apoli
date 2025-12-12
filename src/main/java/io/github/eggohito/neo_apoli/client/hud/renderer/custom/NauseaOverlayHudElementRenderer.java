package io.github.eggohito.neo_apoli.client.hud.renderer.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.eggohito.neo_apoli.client.hud.renderer.OverlayHudElementRenderer;
import io.github.eggohito.neo_apoli.client.util.NeoApoliRenderTypes;
import io.github.eggohito.neo_apoli.client.util.atlas.NeoApoliAtlases;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.custom.NauseaOverlayHudElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public record NauseaOverlayHudElementRenderer() implements OverlayHudElementRenderer {

	@Override
	public void start(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof NauseaOverlayHudElement nauseaOverlay) || !this.visibleBasedOnPerspective(context, nauseaOverlay)) {
			return;
		}

		float scaledWidth = graphics.guiWidth();
		float scaledHeight = graphics.guiHeight();

		Context colorContext = context.makeChild(".color");
		int color = nauseaOverlay.color().getValue(colorContext);

		float intensity = ARGB.alphaFloat(color);
		float red = ARGB.redFloat(color) * intensity;
		float green = ARGB.greenFloat(color) * intensity;
		float blue = ARGB.blueFloat(color) * intensity;

		Material spriteMaterial = NeoApoliAtlases.OVERLAY.getMaterial(nauseaOverlay.sprite());
		TextureAtlasSprite sprite = spriteMaterial.sprite();

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

		int alphaColor = ARGB.colorFromFloat(1.0F, red, green, blue);

		graphics.pose().pushPose();

		Matrix4f matrices = graphics.pose().last().pose();
		VertexConsumer vertexBuffer = spriteMaterial.buffer(Minecraft.getInstance().renderBuffers().bufferSource(), NeoApoliRenderTypes.GUI_TEXTURED_NAUSEA_OVERLAY);

		vertexBuffer.addVertex(matrices, x1, y1, -1.0F).setUv(minU, minV).setColor(alphaColor);
		vertexBuffer.addVertex(matrices, x1, y2, -1.0F).setUv(minU, maxV).setColor(alphaColor);
		vertexBuffer.addVertex(matrices, x2, y2, -1.0F).setUv(maxU, maxV).setColor(alphaColor);
		vertexBuffer.addVertex(matrices, x2, y1, -1.0F).setUv(maxU, minV).setColor(alphaColor);

		graphics.pose().popPose();

	}

	@Override
	public void end(GuiGraphics graphics, DeltaTracker delta) {

	}

}
