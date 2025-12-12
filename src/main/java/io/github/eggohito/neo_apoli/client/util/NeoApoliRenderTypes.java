package io.github.eggohito.neo_apoli.client.util;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

import java.util.function.Function;

public class NeoApoliRenderTypes {

	public static final Function<ResourceLocation, RenderType> GUI_TEXTURED_NAUSEA_OVERLAY = Util.memoize(id -> RenderType.create(
		"neo-apoli:gui_textured_nausea_overlay",
		1536,
		RenderPipelines.GUI_NAUSEA_OVERLAY,
		RenderType.CompositeState.builder()
			.setTextureState(new RenderStateShard.TextureStateShard(id, TriState.DEFAULT, false))
			.createCompositeState(false)
	));

}
