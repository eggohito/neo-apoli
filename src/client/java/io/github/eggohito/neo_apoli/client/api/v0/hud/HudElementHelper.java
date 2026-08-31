package io.github.eggohito.neo_apoli.client.api.v0.hud;

import io.github.eggohito.neo_apoli.client.api.v0.hud.renderer.HudElementRenderer;
import io.github.eggohito.neo_apoli.client.api.v0.hud.source.HudElementSource;
import io.github.eggohito.neo_apoli.client.impl.hud.HudElementHelperImpl;
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
