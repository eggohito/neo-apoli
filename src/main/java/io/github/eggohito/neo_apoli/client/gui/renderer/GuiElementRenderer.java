package io.github.eggohito.neo_apoli.client.gui.renderer;

import io.github.eggohito.neo_apoli.gui.GuiElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface GuiElementRenderer {

	void start(Context context, GuiElement element, GuiGraphics graphics, DeltaTracker delta);

	void end(GuiGraphics graphics, DeltaTracker delta);

}
