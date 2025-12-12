package io.github.eggohito.neo_apoli.client.hud.renderer.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.eggohito.neo_apoli.client.hud.renderer.OverlayHudElementRenderer;
import io.github.eggohito.neo_apoli.client.util.atlas.NeoApoliAtlases;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.custom.TextureOverlayHudElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import org.joml.Matrix4f;

public record TextureOverlayHudElementRenderer() implements OverlayHudElementRenderer {

	@Override
	public void start(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof TextureOverlayHudElement textureOverlay) || !this.visibleBasedOnPerspective(context, textureOverlay)) {
			return;
		}

		float scaledWidth = graphics.guiWidth();
		float scaledHeight = graphics.guiHeight();

		Context colorContext = context.makeChild(".color");
		int color = textureOverlay.color().getValue(colorContext);

		Material spriteMaterial = NeoApoliAtlases.OVERLAY.getMaterial(textureOverlay.sprite());
		TextureAtlasSprite sprite = spriteMaterial.sprite();

		float x1 = 0;
		float x2 = x1 + scaledWidth;

		float y1 = 0;
		float y2 = y1 + scaledHeight;

		float minU = sprite.getU0();
		float maxU = sprite.getU1();

		float minV = sprite.getV0();
		float maxV = sprite.getV1();

		graphics.pose().pushPose();

		Matrix4f matrices = graphics.pose().last().pose();
		VertexConsumer vertexBuffer = spriteMaterial.buffer(Minecraft.getInstance().renderBuffers().bufferSource(), RenderType::guiTexturedOverlay);

		vertexBuffer.addVertex(matrices, x1, y1, -1.0F).setUv(minU, minV).setColor(color);
		vertexBuffer.addVertex(matrices, x1, y2, -1.0F).setUv(minU, maxV).setColor(color);
		vertexBuffer.addVertex(matrices, x2, y2, -1.0F).setUv(maxU, maxV).setColor(color);
		vertexBuffer.addVertex(matrices, x2, y1, -1.0F).setUv(maxU, minV).setColor(color);

		graphics.pose().popPose();

	}

	@Override
	public void end(GuiGraphics graphics, DeltaTracker delta) {

	}

}
