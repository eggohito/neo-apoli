package io.github.eggohito.neo_apoli.client.event;

import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class HudElementRendererEvents {

	public static final Event<Prepare> PREPARE = EventFactory.createArrayBacked(
		Prepare.class,
		callbacks -> (prepare, renderPhase, adder) -> {

			for (var callback : callbacks) {
				callback.prepare(prepare, renderPhase, adder);
			}

		}
	);

	public static final Event<Init> INIT = EventFactory.createArrayBacked(
		Init.class,
		callbacks -> (graphics, delta) -> {

			for (var callback : callbacks) {
				callback.init(graphics, delta);
			}

		}
	);

	public static final Event<Render> RENDER = EventFactory.createArrayBacked(
		Render.class,
		callbacks -> (context, element, graphics, delta) -> {

			for (var callback : callbacks) {
				callback.render(context, element, graphics, delta);
			}

		}
	);

	public interface Prepare {
		void prepare(Consumer<Consumer<Power.Instance<?>>> prepare, HudRenderPhase renderPhase, BiConsumer<Context, HudElement> adder);
	}

	public interface Init {
		void init(GuiGraphics graphics, DeltaTracker delta);
	}

	public interface Render {
		void render(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta);
	}

}
