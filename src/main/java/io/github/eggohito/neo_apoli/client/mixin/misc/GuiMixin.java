package io.github.eggohito.neo_apoli.client.mixin.misc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.client.hud.renderer.HudElementRenderer;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BooleanSupplier;

@Mixin(Gui.class)
public abstract class GuiMixin {

	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDraw;add(Lnet/minecraft/client/gui/LayeredDraw;Ljava/util/function/BooleanSupplier;)Lnet/minecraft/client/gui/LayeredDraw;", ordinal = 0))
	private LayeredDraw addBeforeLayer(LayeredDraw layers, LayeredDraw layer, BooleanSupplier condition, Operation<LayeredDraw> original, Minecraft minecraft) {
		return original.call(layers.add(new HudElementRenderer.Layer(HudRenderPhase.BELOW_HUD)), layer, condition);
	}

	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDraw;add(Lnet/minecraft/client/gui/LayeredDraw;Ljava/util/function/BooleanSupplier;)Lnet/minecraft/client/gui/LayeredDraw;", ordinal = 1))
	private LayeredDraw addAfterLayer(LayeredDraw layers, LayeredDraw layer, BooleanSupplier condition, Operation<LayeredDraw> original) {
		return original.call(layers.add(new HudElementRenderer.Layer(HudRenderPhase.ABOVE_HUD)), layer, condition);
	}

}
