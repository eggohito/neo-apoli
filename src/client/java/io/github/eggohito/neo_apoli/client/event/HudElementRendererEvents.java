package io.github.eggohito.neo_apoli.client.event;

import io.github.eggohito.neo_apoli.client.api.hud.renderer.HudElementRenderer;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.BiConsumer;

public class HudElementRendererEvents {

	public static final Event<Prepare> PREPARE = EventFactory.createArrayBacked(
		Prepare.class,
		callbacks -> (prepare, renderPhase, adder) -> {

			for (var callback : callbacks) {
				callback.prepare(prepare, renderPhase, adder);
			}

		}
	);

	public static final Event<HudElementRenderer> RENDER = EventFactory.createArrayBacked(
		HudElementRenderer.class,
		callbacks -> new HudElementRenderer() {

			@Override
			public void init(GuiGraphics graphics, DeltaTracker delta) {

				for (var callback : callbacks) {
					callback.init(graphics, delta);
				}

			}

			@Override
			public void render(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta) {

				for (var callback : callbacks) {
					callback.render(context, element, graphics, delta);
				}

			}

		}
	);

	public interface Prepare {
		void prepare(Power.Instance<?> instance, HudRenderPhase renderPhase, BiConsumer<Context, HudElement> adder);
	}

}
