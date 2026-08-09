package io.github.eggohito.neo_apoli.client.impl.hud.renderer;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.client.event.HudElementRendererEvents;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public enum HudElementLayer implements IdentifiedLayer {

	ABOVE_HUD("hud_element/above_hud", HudElement.RenderPhase.ABOVE_HUD),
	BELOW_HUD("hud_element/below_hud", HudElement.RenderPhase.BELOW_HUD);

	private final ResourceLocation id;
	private final HudElement.RenderPhase renderPhase;

	HudElementLayer(String path, HudElement.RenderPhase renderPhase) {
		this.id = NeoApoli.id(path);
		this.renderPhase = renderPhase;
	}

	@Override
	public ResourceLocation id() {
		return id;
	}

	@Override
	public void render(GuiGraphics graphics, DeltaTracker delta) {

		Player player = Minecraft.getInstance().player;
		Powers powers = Powers.getNullable(player);

		if (powers == null) {
			return;
		}

		Map<HudElement.Type<?>, List<Instance>> queue = new Reference2ObjectArrayMap<>();

		for (var instance : powers.getAllInstances()) {
			HudElementRendererEvents.PREPARE.invoker().prepare(
				player,
				instance,
				renderPhase,
				(context, element) -> queue
					.computeIfAbsent(element.getType(), k -> new ObjectArrayList<>())
					.add(new Instance(context, element))
			);
		}

		for (var entry : queue.entrySet()) {

			List<Instance> instances = entry.getValue();
			instances.sort(Instance::compareTo);

			HudElementRendererEvents.RENDER.invoker().init(graphics, delta);
			instances.forEach(instance -> instance.render(graphics, delta));

		}

	}

	public record Instance(Context context, HudElement element) implements Comparable<Instance>, LayeredDraw.Layer {

		@Override
		public int compareTo(@NotNull HudElementLayer.Instance that) {
			return Integer.compare(this.element().order(), that.element().order());
		}

		@Override
		public void render(GuiGraphics graphics, DeltaTracker delta) {

			graphics.pose().pushPose();

			HudElementRendererEvents.RENDER.invoker().render(context(), element(), graphics, delta);
			graphics.pose().translate(0.0F, 0.0F, 200.0F);

			graphics.pose().popPose();

		}

	}

}
