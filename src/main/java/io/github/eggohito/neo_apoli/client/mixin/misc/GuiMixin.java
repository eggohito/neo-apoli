package io.github.eggohito.neo_apoli.client.mixin.misc;

import io.github.eggohito.neo_apoli.client.event.HudElementRenderEvents;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;

@Mixin(Gui.class)
public abstract class GuiMixin {

	@Inject(method = "renderHotbarAndDecorations", at = @At("TAIL"))
	private void renderCustomHudElements(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {

		LocalPlayer player = Minecraft.getInstance().player;
		PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.maybeGet(player).orElse(null);

		if (player == null || powersComponent == null) {
			return;
		}

		//	Prepare all the HUD elements and pair them with the power instance's context
		List<Pair<Context, HudElement>> renderQueue = new ObjectArrayList<>();
		HudElementRenderEvents.PREPARE.invoker().prepare(NeoApoliEntityComponents.POWERS.get(player), (ctx, hud) -> renderQueue.add(Pair.of(ctx, hud)));

		//	Sort all the paired HUD elements depending on their specified order
		Comparator<Pair<Context, HudElement>> comparator = Comparator.comparingInt(pair -> pair.right().order());
		renderQueue.sort(comparator.reversed());

		for (var pair : renderQueue) {

			Context context = pair.left();
			HudElement hudElement = pair.right();

			HudElementRenderEvents.START.invoker().start(context, hudElement, graphics, delta);

		}

		if (!renderQueue.isEmpty()) {
			HudElementRenderEvents.END.invoker().end(graphics, delta);
		}

	}

}
