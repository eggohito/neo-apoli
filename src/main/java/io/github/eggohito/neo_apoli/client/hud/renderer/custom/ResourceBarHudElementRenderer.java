package io.github.eggohito.neo_apoli.client.hud.renderer.custom;

import io.github.eggohito.neo_apoli.client.NeoApoliClient;
import io.github.eggohito.neo_apoli.client.hud.renderer.HudElementRenderer;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.custom.ResourceBarHudElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record ResourceBarHudElementRenderer() implements HudElementRenderer {

	private static final int BAR_WIDTH = 72;
	private static final int BAR_HEIGHT = 8;
	private static final int ICON_SIZE = 8;

	private static HudElement.Position anchorPos;

	@Override
	public void start(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof ResourceBarHudElement resourceBar)) {
			return;
		}

		ResourceBarHudElement.Properties properties = resourceBar.properties();
		ResourceBarHudElement.SpriteLocation spriteLocation = properties.spriteLocation();

		if (anchorPos == null) {
			init(context, element, graphics, delta);
		}

		int x = anchorPos.getX() + resourceBar.x().nextInt(context.forChild(".x"));
		int y = anchorPos.getY() + resourceBar.y().nextInt(context.forChild(".y"));

		//	Draw the background texture of the bar
		graphics.blitSprite(RenderType::guiTextured, spriteLocation.background(), x, y - 2, BAR_WIDTH, BAR_HEIGHT);

		//	Draw the fill portion of the bar
		graphics.blitSprite(RenderType::guiTextured, spriteLocation.fill(), BAR_WIDTH, BAR_HEIGHT, 0, 0, x, y - 2, (int) (resourceBar.getFill(context) * BAR_WIDTH), BAR_HEIGHT);

		//	Draw the icon of the bar
		graphics.blitSprite(RenderType::guiTextured, spriteLocation.icon(), x - ICON_SIZE - 2, y - 2, ICON_SIZE, ICON_SIZE);

		//	Shift the Y anchor position upwards by the height of the rendered bar if a bar sprite is within the anchor space
		if (((x + BAR_WIDTH) >= anchorPos.getX() && x <= (anchorPos.getX() + BAR_WIDTH)) && ((y + BAR_HEIGHT >= anchorPos.getY()) && y <= (anchorPos.getY() + BAR_HEIGHT))) {
			anchorPos.setY(anchorPos.getY() - BAR_HEIGHT);
		}

	}

	@Override
	public void end(GuiGraphics graphics, DeltaTracker delta) {
		anchorPos = null;
	}

	private void init(Context context, HudElement ignoredElement, GuiGraphics graphics, DeltaTracker ignoredDelta) {

		Entity viewer = context.required(NeoApoliContextKeys.THIS_ENTITY);
		int yOffset = 49;

		//	Shift the bar anchor point upwards to make room for the air bar
		if (viewer.isEyeInFluid(FluidTags.WATER) && viewer.getAirSupply() < viewer.getMaxAirSupply()) {
			yOffset += 10;
		}

		//	Shift the bar anchor point upwards to make room for the vehicle's health bar(s)
		if (viewer.getVehicle() instanceof LivingEntity livingVehicle) {

			int icons = (int) Math.ceil(livingVehicle.getMaxHealth() / 20.0F);
			int rows = Mth.clamp(icons, 1, 3);

			yOffset += rows * 10;

		}

		anchorPos = new HudElement.Position();

		anchorPos.setX(((graphics.guiWidth() / 2) + 20) + NeoApoliClient.getConfig().resourceBars.offsetX);
		anchorPos.setY((graphics.guiHeight() - yOffset) + NeoApoliClient.getConfig().resourceBars.offsetY);

	}

}
