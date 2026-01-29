package io.github.eggohito.neo_apoli.client.api.hud.renderer;

import io.github.eggohito.neo_apoli.client.event.HudElementRendererEvents;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.type.HudElementType;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface HudElementRenderer extends HudElementRendererEvents.Render, HudElementRendererEvents.Init {

	record Layer(HudRenderPhase renderPhase) implements LayeredDraw.Layer {

		@Override
		public void render(GuiGraphics graphics, DeltaTracker delta) {

			PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.maybeGet(Minecraft.getInstance().player).orElse(null);
			Map<HudElementType<?>, List<Instance>> queue = new Reference2ObjectArrayMap<>();

			if (powersComponent == null) {
				return;
			}

			HudElementRendererEvents.PREPARE.invoker().prepare(
				powersComponent.getAllInstances()::forEach,
				renderPhase(),
				(context, hudElement) -> queue
					.computeIfAbsent(hudElement.getType(), k -> new ObjectArrayList<>())
					.add(new Instance(context, hudElement))
			);

			for (var entry : queue.entrySet()) {

				List<Instance> instances = entry.getValue();
				instances.sort(Instance::compareTo);

				HudElementRendererEvents.INIT.invoker().init(graphics, delta);
				instances.forEach(instance -> instance.render(graphics, delta));

			}

		}

	}

	record Instance(Context context, HudElement hudElement) implements Comparable<Instance>, LayeredDraw.Layer {

		@Override
		public int compareTo(@NotNull HudElementRenderer.Instance that) {
			 return Integer.compare(this.hudElement().order(), that.hudElement().order());
		}

		@Override
		public void render(GuiGraphics graphics, DeltaTracker delta) {

			graphics.pose().pushPose();

			HudElementRendererEvents.RENDER.invoker().render(context(), hudElement(), graphics, delta);
			graphics.pose().translate(0.0f, 0.0f, 200.0f);

			graphics.pose().popPose();

		}

	}

}
