package io.github.eggohito.neo_apoli.client.gui.renderer;

import io.github.eggohito.neo_apoli.gui.GuiElement;
import io.github.eggohito.neo_apoli.gui.custom.BarGuiElement;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record BarGuiElementRenderer() implements GuiElementRenderer {

	private static GuiElement.Position anchorPos;

	@Override
	public void start(Context context, GuiElement element, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof BarGuiElement barGuiElement)) {
			return;
		}

		BarGuiElement.Properties properties = barGuiElement.properties();
		BarGuiElement.SpriteLocation spriteLocation = properties.spriteLocation();

		if (!properties.shouldRender().next(context.makeChild(".should_render"))) {
			return;
		}

		if (anchorPos == null) {
			init(context, element, graphics, delta);
		}

		int x = anchorPos.getX() + spriteLocation.x().nextInt(context.makeChild(".x"));
		int y = anchorPos.getY() + spriteLocation.y().nextInt(context.makeChild(".y"));

		//	Draw the background texture of the bar
		graphics.blitSprite(RenderType::guiTextured, spriteLocation.background(), x, y - 2, BarGuiElement.BAR_WIDTH, BarGuiElement.BAR_HEIGHT);

		//	Draw the fill portion of the bar
		graphics.blitSprite(RenderType::guiTextured, spriteLocation.fill(), BarGuiElement.BAR_WIDTH, BarGuiElement.BAR_HEIGHT, 0, 0, x, y - 2, (int) (barGuiElement.getFill(context) * BarGuiElement.BAR_WIDTH), BarGuiElement.BAR_HEIGHT);

		//	Draw the icon of the bar
		graphics.blitSprite(RenderType::guiTextured, spriteLocation.icon(), x - BarGuiElement.ICON_SIZE - 2, y - 2, BarGuiElement.ICON_SIZE, BarGuiElement.ICON_SIZE);

		//	Shift the Y anchor position upwards by the height of the rendered bar if a bar sprite is within the anchor space
		if (((x + BarGuiElement.BAR_WIDTH) >= anchorPos.getX() && x <= (anchorPos.getX() + BarGuiElement.BAR_WIDTH)) && ((y + BarGuiElement.BAR_HEIGHT >= anchorPos.getY()) && y <= (anchorPos.getY() + BarGuiElement.BAR_HEIGHT))) {
			anchorPos.setY(anchorPos.getY() - BarGuiElement.BAR_HEIGHT);
		}

	}

	@Override
	public void end(GuiGraphics graphics, DeltaTracker delta) {
		anchorPos = null;
	}

	private static void init(Context context, GuiElement ignoredElement, GuiGraphics graphics, DeltaTracker ignoredDelta) {

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

		anchorPos = new GuiElement.Position();

		anchorPos.setX((graphics.guiWidth() / 2) + 20);
		anchorPos.setY(graphics.guiHeight() - yOffset);

	}

}
