package io.github.eggohito.neo_apoli.client.mixin.misc;

import io.github.eggohito.neo_apoli.client.impl.hud.renderer.HudElementLayer;
import io.github.eggohito.neo_apoli.client.mixin.access.LayeredDrawAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

	@Shadow
	@Final
	private LayeredDraw layers;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void addBelowHudLayer(Minecraft minecraft, CallbackInfo ci) {

		LayeredDrawAccessor accessor = (LayeredDrawAccessor) this.layers;

		accessor.getLayers().add(1, HudElementLayer.ABOVE_HUD);
		accessor.getLayers().addFirst(HudElementLayer.BELOW_HUD);

	}

}
