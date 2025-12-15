package io.github.eggohito.neo_apoli.client.hud.renderer;

import io.github.eggohito.neo_apoli.hud.OverlayHudElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.Minecraft;

public interface OverlayHudElementRenderer extends HudElementRenderer {

	default boolean visibleBasedOnPerspective(Context context, OverlayHudElement overlay) {
		return Minecraft.getInstance().options.getCameraType().isFirstPerson()
			|| overlay.visibleInThirdPerson().next(context.forChild(".visible_in_third_person"));
	}

}
