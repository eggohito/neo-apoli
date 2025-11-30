package io.github.eggohito.neo_apoli.client.integration;

import io.github.eggohito.neo_apoli.gui.GuiElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public class GuiElementRenderEvents {

	public static final Event<Start> START = EventFactory.createArrayBacked(
		Start.class,
		callbacks -> (context, element, graphics, delta) -> {

			for (var callback : callbacks) {
				callback.render(context, element, graphics, delta);
			}

		}
	);

	public static final Event<End> END = EventFactory.createArrayBacked(
		End.class,
		callbacks -> (graphics, delta) -> {

			for (var callback : callbacks) {
				callback.render(graphics, delta);
			}

		}
	);

	public interface Start {
		void render(Context context, GuiElement element, GuiGraphics graphics, DeltaTracker delta);
	}

	public interface End {
		void render(GuiGraphics graphics, DeltaTracker delta);
	}

}
