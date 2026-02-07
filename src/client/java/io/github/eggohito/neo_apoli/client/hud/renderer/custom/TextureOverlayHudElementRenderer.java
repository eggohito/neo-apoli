package io.github.eggohito.neo_apoli.client.hud.renderer.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.client.api.hud.renderer.OverlayHudElementRenderer;
import io.github.eggohito.neo_apoli.client.util.NeoApoliRenderTypes;
import io.github.eggohito.neo_apoli.client.util.SpriteMaterial;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.custom.TextureOverlayHudElement;
import io.github.eggohito.neo_apoli.util.Reporter;
import lombok.NoArgsConstructor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;
import org.slf4j.event.Level;

@NoArgsConstructor
public final class TextureOverlayHudElementRenderer implements OverlayHudElementRenderer {

	@Override
	public void render(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof TextureOverlayHudElement textureOverlay) || !this.visibleBasedOnPerspective(context, textureOverlay)) {
			return;
		}

		Reporter reporter = context.reporter();
		SpriteMaterial spriteMaterial = new SpriteMaterial(textureOverlay.sprite());

		TextureAtlasSprite sprite = spriteMaterial.spriteAsResult()
			.resultOrPartial(reporter::report)
			.orElse(null);

		if (sprite == null || reporter.hasErrors()) {

			reporter.getErrorsFlattened().ifPresent(error -> NeoApoli.logOnce(Level.ERROR, "Error trying to render overlay HUD element(s) due to error(s) " + error));

			return;

		}

		Context colorContext = context.forChild(".color");
		int color = textureOverlay.color().intValue(colorContext);

		float x1 = 0.0F;
		float x2 = x1 + graphics.guiWidth();

		float y1 = 0.0F;
		float y2 = y1 + graphics.guiHeight();

		float minU = sprite.getU0();
		float maxU = sprite.getU1();

		float minV = sprite.getV0();
		float maxV = sprite.getV1();

		Matrix4f matrices = graphics.pose().last().pose();
		VertexConsumer vertexBuffer = spriteMaterial.buffer(Minecraft.getInstance().renderBuffers().bufferSource(), NeoApoliRenderTypes.GUI_TEXTURED_OVERLAY);

		vertexBuffer.addVertex(matrices, x1, y1, 0.0F).setUv(minU, minV).setColor(color);
		vertexBuffer.addVertex(matrices, x1, y2, 0.0F).setUv(minU, maxV).setColor(color);
		vertexBuffer.addVertex(matrices, x2, y2, 0.0F).setUv(maxU, maxV).setColor(color);
		vertexBuffer.addVertex(matrices, x2, y1, 0.0F).setUv(maxU, minV).setColor(color);

		reporter.getErrorsFlattened().ifPresent(warn -> NeoApoli.logOnce(Level.WARN, "Found warnings when rendering overlay HUD element(s) " + warn));

	}

}
