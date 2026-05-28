package io.github.eggohito.neo_apoli.client.api.hud.renderer;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.OverlayHudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public interface OverlayHudElementRenderer extends HudElementRenderer {

	@Override
	default void init(GuiGraphics graphics, DeltaTracker delta) {

	}

	default boolean isVisibleInPerspective(Context context, OverlayHudElement overlay) {
		return Minecraft.getInstance().options.getCameraType().isFirstPerson()
			|| overlay.visibleInThirdPerson().getBoolean(context.forChild(".visible_in_third_person"));
	}

}
