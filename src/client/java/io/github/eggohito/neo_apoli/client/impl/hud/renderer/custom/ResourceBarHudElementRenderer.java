package io.github.eggohito.neo_apoli.client.impl.hud.renderer.custom;

import io.github.eggohito.neo_apoli.client.api.hud.renderer.HudElementRenderer;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.custom.ResourceBarHudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public enum ResourceBarHudElementRenderer implements HudElementRenderer {

	INSTANCE;

	private static final HudElement.Position ANCHOR = new HudElement.Position();
	private static final int BAR_HEIGHT = 8;
	private static final int BAR_WIDTH = 72;
	private static final int ICON_SIZE = 8;

	@Override
	public void init(GuiGraphics graphics, DeltaTracker delta) {

		Entity viewer = Objects.requireNonNull(Minecraft.getInstance().player);
		int yOffset = 49;

		if (viewer.isEyeInFluid(FluidTags.WATER) && viewer.getAirSupply() < viewer.getMaxAirSupply()) {
			yOffset += 10;
		}

		if (viewer.getVehicle() instanceof LivingEntity livingVehicle) {

			int icons = (int) Math.ceil(livingVehicle.getMaxHealth() / 20.0F);
			int rows = Mth.clamp(icons, 1, 3);

			yOffset += rows * 10;

		}

		ANCHOR.setX(((graphics.guiWidth() / 2) + 20) + ResourceBarHudElement.Config.INSTANCE.offsetX.get());
		ANCHOR.setY((graphics.guiHeight() - yOffset) + ResourceBarHudElement.Config.INSTANCE.offsetY.get());

	}

	@Override
	public void render(Context context, HudElement element, GuiGraphics graphics, DeltaTracker delta) {

		if (!(element instanceof ResourceBarHudElement resourceBar)) {
			return;
		}

		ResourceBarHudElement.Properties properties = resourceBar.properties();
		ResourceBarHudElement.SpriteLocation spriteLocation = properties.spriteLocation();

		int x = ANCHOR.getX() + resourceBar.x().nextInt(context.forChild(".x"));
		int y = ANCHOR.getY() + resourceBar.y().nextInt(context.forChild(".y"));

		//	Draw the background texture of the bar
		graphics.blitSprite(RenderType::guiTextured, spriteLocation.background(), x, y - 2, BAR_WIDTH, BAR_HEIGHT);

		//	Draw the fill portion of the bar
		graphics.blitSprite(RenderType::guiTextured, spriteLocation.fill(), BAR_WIDTH, BAR_HEIGHT, 0, 0, x, y - 2, (int) (resourceBar.getFill(context) * BAR_WIDTH), BAR_HEIGHT);

		//	Draw the icon of the bar
		graphics.blitSprite(RenderType::guiTextured, spriteLocation.icon(), x - ICON_SIZE - 2, y - 2, ICON_SIZE, ICON_SIZE);

		//	Shift the Y anchor position upwards by the height of the rendered bar if a bar sprite is within the anchor space
		if (withinAnchorSpace(x, y)) {
			ANCHOR.setY(ANCHOR.getY() - BAR_HEIGHT);
		}

	}

	private boolean withinAnchorSpace(int x, int y) {
		return x + BAR_WIDTH >= ANCHOR.getX() && x <= ANCHOR.getX() + BAR_WIDTH
			&& y + BAR_HEIGHT >= ANCHOR.getY() && y <= ANCHOR.getY() + BAR_HEIGHT;
	}

}
