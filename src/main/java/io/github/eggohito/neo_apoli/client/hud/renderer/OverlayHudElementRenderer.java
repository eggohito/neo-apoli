package io.github.eggohito.neo_apoli.client.hud.renderer;

import io.github.eggohito.neo_apoli.hud.OverlayHudElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public interface OverlayHudElementRenderer extends HudElementRenderer {

	@Override
	default void init(GuiGraphics graphics, DeltaTracker delta) {

	}

	default boolean visibleBasedOnPerspective(Context context, OverlayHudElement overlay) {
		return Minecraft.getInstance().options.getCameraType().isFirstPerson()
			|| overlay.visibleInThirdPerson().next(context.forChild(".visible_in_third_person"));
	}

}
