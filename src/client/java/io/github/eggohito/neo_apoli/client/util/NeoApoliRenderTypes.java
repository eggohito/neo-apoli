package io.github.eggohito.neo_apoli.client.util;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

import java.util.function.Function;

public class NeoApoliRenderTypes {

	public static final Function<ResourceLocation, RenderType> GUI_NAUSEA_OVERLAY = Util.memoize(id -> RenderType.create(
		"neo-apoli:gui_overlay/nausea",
		1536,
		NeoApoliRenderPipelines.GUI_NAUSEA_OVERLAY,
		RenderType.CompositeState.builder()
			.setTextureState(new RenderStateShard.TextureStateShard(id, TriState.DEFAULT, false))
			.createCompositeState(false)
	));

	public static final Function<ResourceLocation, RenderType> GUI_TEXTURED_OVERLAY = Util.memoize(id -> RenderType.create(
		"neo-apoli:gui_overlay/textured",
		1536,
		NeoApoliRenderPipelines.GUI_TEXTURED_OVERLAY,
		RenderType.CompositeState.builder()
			.setTextureState(new RenderStateShard.TextureStateShard(id, TriState.DEFAULT, false))
			.createCompositeState(false)
	));

}
