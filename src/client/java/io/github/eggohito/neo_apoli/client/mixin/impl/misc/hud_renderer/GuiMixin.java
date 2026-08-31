package io.github.eggohito.neo_apoli.client.mixin.impl.misc.hud_renderer;

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

//  Priority is lower than Fabric API for compatibility reasons (if addons ever want to add a layer before or after
//  the hardcoded HUD element layers.)
//
//  TODO:   Migrate to Fabric API's HUD layer registration API once it can add before/after a layer without inheriting
//          its render condition.
@Mixin(value = Gui.class, priority = 999)
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
