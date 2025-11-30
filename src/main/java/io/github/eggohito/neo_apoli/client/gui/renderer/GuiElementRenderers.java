package io.github.eggohito.neo_apoli.client.gui.renderer;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.client.integration.GuiElementRenderEvents;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class GuiElementRenderers {

	public static final BarGuiElementRenderer BAR = registerInternal("gui_element/bar", BarGuiElementRenderer::new);

	public static void registerAll() {

	}

	private static <R extends BarGuiElementRenderer> R registerInternal(String path, Supplier<R> rendererSupplier) {
		return register(NeoApoli.id(path), rendererSupplier);
	}

	public static <R extends BarGuiElementRenderer> R register(ResourceLocation id, Supplier<R> rendererSupplier) {

		R renderer = rendererSupplier.get();

		GuiElementRenderEvents.START.register(id, renderer::start);
		GuiElementRenderEvents.END.register(id, renderer::end);

		return renderer;

	}

}
