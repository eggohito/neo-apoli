package io.github.eggohito.neo_apoli.client.hud.renderer.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.eggohito.neo_apoli.client.hud.renderer.OverlayHudElementRenderer;
import io.github.eggohito.neo_apoli.client.util.SpriteMaterial;
import io.github.eggohito.neo_apoli.client.util.registry.NeoApoliRenderTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.element.OverlayHudElement;
import io.github.eggohito.neo_apoli.hud.element.custom.NauseaOverlayHudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public enum NauseaOverlayHudElementRenderer implements OverlayHudElementRenderer {

	INSTANCE;

	@Override
	public void renderOverlay(Context context, OverlayHudElement element, SpriteMaterial material, TextureAtlasSprite sprite, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof NauseaOverlayHudElement nauseaOverlay)) {
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
		VertexConsumer vertexBuffer = material.buffer(Minecraft.getInstance().renderBuffers().bufferSource(), NeoApoliRenderTypes.GUI_NAUSEA_OVERLAY);

		vertexBuffer.addVertex(matrices, x1, y1, 0.0F).setUv(minU, minV).setColor(color);
		vertexBuffer.addVertex(matrices, x1, y2, 0.0F).setUv(minU, maxV).setColor(color);
		vertexBuffer.addVertex(matrices, x2, y2, 0.0F).setUv(maxU, maxV).setColor(color);
		vertexBuffer.addVertex(matrices, x2, y1, 0.0F).setUv(maxU, minV).setColor(color);

	}

}
