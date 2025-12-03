package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import io.github.eggohito.neo_apoli.client.integration.HudElementRenderEvents;
import io.github.eggohito.neo_apoli.power.custom.HudRenderPower;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class HudRenderPowerMixin {

	@Inject(method = "renderHotbarAndDecorations", at = @At("TAIL"))
	private void render(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {

		LocalPlayer viewer = Minecraft.getInstance().player;
		Prioritized.InstanceCollection<HudRenderPower.Instance> instances = new Prioritized.InstanceCollection<>(viewer, HudRenderPower.Instance.class);

		for (var instance : instances) {

			Context context = instance.createHolderContext();

			try {

				if (context.markActive(instance)) {
					HudElementRenderEvents.START.invoker().start(context, instance.getHudElement(), graphics, delta);
				}

			}

			finally {
				context.markInActive(instance);
			}

		}

		if (!instances.isEmpty()) {
			HudElementRenderEvents.END.invoker().end(graphics, delta);
		}

	}

}
