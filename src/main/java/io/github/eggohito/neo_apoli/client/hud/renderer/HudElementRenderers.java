package io.github.eggohito.neo_apoli.client.hud.renderer;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.client.event.HudElementRenderEvents;
import io.github.eggohito.neo_apoli.client.hud.renderer.custom.ResourceBarHudElementRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class HudElementRenderers {

	public static void registerAll() {
		registerInternal("resource_bar", ResourceBarHudElementRenderer::new);
	}

	private static <R extends HudElementRenderer> void registerInternal(String path, Supplier<R> rendererSupplier) {
		register(NeoApoli.id(path), rendererSupplier);
	}

	public static <R extends HudElementRenderer> void register(ResourceLocation id, Supplier<R> rendererSupplier) {

		R renderer = rendererSupplier.get();

		HudElementRenderEvents.START.register(id, renderer);
		HudElementRenderEvents.END.register(id, renderer);

	}

}
