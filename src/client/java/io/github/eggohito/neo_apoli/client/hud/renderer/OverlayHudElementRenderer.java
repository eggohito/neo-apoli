package io.github.eggohito.neo_apoli.client.hud.renderer;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.client.util.SpriteMaterial;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.element.HudElement;
import io.github.eggohito.neo_apoli.hud.element.OverlayHudElement;
import io.github.eggohito.neo_apoli.util.Reporter;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.slf4j.event.Level;

public interface OverlayHudElementRenderer extends HudElementRenderer {

	@Override
	default void init(GuiGraphics graphics, DeltaTracker delta) {

	}

	@Override
	default void render(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof OverlayHudElement overlay) || thouShouldNotRender(context, overlay)) {
			return;
		}

		Reporter reporter = context.reporter();
		SpriteMaterial material = new SpriteMaterial(overlay.sprite());

		material
			.spriteAsResult()
			.resultOrPartial(reporter::report).ifPresent(sprite -> this.renderOverlay(context, overlay, material, sprite, graphics, delta));

		if (reporter.hasProblems()) {
			NeoApoli.logOnce(Level.WARN, "Found warnings while rendering overlay HUD elements\n" + reporter.getReport());
		}

	}

	void renderOverlay(Context context, OverlayHudElement element, SpriteMaterial material, TextureAtlasSprite sprite, GuiGraphics graphics, DeltaTracker delta);

	private static boolean thouShouldNotRender(Context context, OverlayHudElement overlay) {
		return !Minecraft.getInstance().options.getCameraType().isFirstPerson()
			&& !overlay.visibleInThirdPerson().getBoolean(context.forChild(".visible_in_third_person"));
	}

}
