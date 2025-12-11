package io.github.eggohito.neo_apoli.client.event;

import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.BiConsumer;

public class HudElementRenderEvents {

	public static final Event<Prepare> PREPARE = EventFactory.createArrayBacked(
		Prepare.class,
		callbacks -> (powersComponent, adder) -> {

			for (var callback : callbacks) {
				callback.prepare(powersComponent, adder);
			}

		}
	);

	public static final Event<Start> START = EventFactory.createArrayBacked(
		Start.class,
		callbacks -> (context, element, graphics, delta) -> {

			for (var callback : callbacks) {
				callback.start(context, element, graphics, delta);
			}

		}
	);

	public static final Event<End> END = EventFactory.createArrayBacked(
		End.class,
		callbacks -> (graphics, delta) -> {

			for (var callback : callbacks) {
				callback.end(graphics, delta);
			}

		}
	);

	public interface Prepare {
		void prepare(PowersComponent powersComponent, BiConsumer<Context, HudElement> adder);
	}

	public interface Start {
		void start(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta);
	}

	public interface End {
		void end(GuiGraphics graphics, DeltaTracker delta);
	}

}
