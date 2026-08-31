package io.github.eggohito.neo_apoli.client.hud;

import io.github.eggohito.neo_apoli.client.hud.internal.HudElementHelperImpl;
import io.github.eggohito.neo_apoli.client.hud.renderer.HudElementRenderer;
import io.github.eggohito.neo_apoli.client.hud.source.HudElementSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface HudElementHelper {

	static void registerRenderer(ResourceLocation id, HudElementRenderer renderer) {
		HudElementHelperImpl.registerRenderer(id, renderer);
	}

	static void registerSource(ResourceLocation id, HudElementSource source) {
		HudElementHelperImpl.registerSource(id, source);
	}

}
