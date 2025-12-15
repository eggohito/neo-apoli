package io.github.eggohito.neo_apoli.client.hud.renderer;

import io.github.eggohito.neo_apoli.client.event.HudElementRenderEvents;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HudElementRenderer extends HudElementRenderEvents.Start, HudElementRenderEvents.End {

	record Layer(HudRenderPhase renderPhase) implements LayeredDraw.Layer {

		@Override
		public void render(GuiGraphics graphics, DeltaTracker delta) {

			PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.maybeGet(Minecraft.getInstance().player).orElse(null);
			List<Instance> queue = new ObjectArrayList<>();

			if (powersComponent == null) {
				return;
			}

			HudElementRenderEvents.PREPARE.invoker().prepare(powersComponent.getAllInstances()::forEach, renderPhase(), (ctx, hud) -> queue.add(new Instance(ctx, hud)));

			if (queue.isEmpty()) {
				return;
			}

			queue.sort(Instance::compareTo);

			for (var instance : queue) {

				graphics.pose().pushPose();

				instance.render(graphics, delta);
				graphics.pose().translate(0.0F, 0.0F, 200.0F);

				graphics.pose().popPose();

			}

			HudElementRenderEvents.END.invoker().end(graphics, delta);

		}

	}

	record Instance(Context context, HudElement hudElement) implements Comparable<Instance>, LayeredDraw.Layer {

		@Override
		public int compareTo(@NotNull HudElementRenderer.Instance that) {
			 return Integer.compare(this.hudElement().order(), that.hudElement().order());
		}

		 @Override
		 public void render(GuiGraphics graphics, DeltaTracker delta) {
			 HudElementRenderEvents.START.invoker().start(context(), hudElement(), graphics, delta);
		 }

	}

}
