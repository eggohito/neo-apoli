package io.github.eggohito.neo_apoli.client.mixin.access;

import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WingsLayer.class)
public interface WingsLayerAccessor {

	@Nullable
	@Invoker
	static ResourceLocation callGetPlayerElytraTexture(HumanoidRenderState renderState) {
		throw new AssertionError();
	}

}
