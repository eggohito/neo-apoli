package io.github.eggohito.neo_apoli.client.api.v0.hud.renderer;

import io.github.eggohito.neo_apoli.api.v0.hud.element.HudElement;
import io.github.eggohito.neo_apoli.context.Context;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface HudElementRenderer {

	void init(GuiGraphics graphics, DeltaTracker delta);

	void render(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta);

}
