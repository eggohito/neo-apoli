package io.github.eggohito.neo_apoli.client.hud.renderer;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.element.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface HudElementRenderer<E extends HudElement> {

	void init(GuiGraphics graphics, DeltaTracker delta);

	void render(Context context, E element, GuiGraphics graphics, DeltaTracker delta);

}
