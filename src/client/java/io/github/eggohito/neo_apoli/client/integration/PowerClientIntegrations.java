package io.github.eggohito.neo_apoli.client.integration;

import io.github.eggohito.neo_apoli.client.event.HudElementRendererEvents;
import io.github.eggohito.neo_apoli.client.renderer.entity.layers.PowerWingsLayer;
import io.github.eggohito.neo_apoli.duck.EntityCache;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.CooldownPower;
import io.github.eggohito.neo_apoli.power.custom.HudRenderPower;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.ListIterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PowerClientIntegrations {

	public static void registerAll() {
		ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> clearEntityTypeCache());
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, level) -> clearEntityTypeCache());
		HudElementRendererEvents.PREPARE.register(PowerClientIntegrations::prepareCooldownElements);
		HudElementRendererEvents.PREPARE.register(PowerClientIntegrations::prepareHudElements);
		LivingEntityFeatureRendererRegistrationCallback.EVENT.register(PowerClientIntegrations::preparePowerWingLayer);
	}

	private static void preparePowerWingLayer(EntityType<? extends LivingEntity> type, LivingEntityRenderer<?, ?, ?> renderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper helper, EntityRendererProvider.Context context) {

		switch (renderer) {
			case HumanoidMobRenderer<?, ?, ?> humanoidMobRenderer ->
				helper.register(new PowerWingsLayer<>(humanoidMobRenderer, context.getModelSet(), context.getEquipmentRenderer()));
			case PlayerRenderer playerRenderer ->
				helper.register(new PowerWingsLayer<>(playerRenderer, context.getModelSet(), context.getEquipmentRenderer()));
			case ArmorStandRenderer armorStandRenderer ->
				helper.register(new PowerWingsLayer<>(armorStandRenderer, context.getModelSet(), context.getEquipmentRenderer()));
			default -> {
				//  No-op; renderer is unsupported
			}
		}

	}

	private static void prepareHudElements(Consumer<Consumer<Power.Instance<?>>> prepare, HudRenderPhase renderPhase, BiConsumer<Context, HudElement> adder) {

		Consumer<Power.Instance<?>> preparer = instance -> {

			if (!(instance instanceof HudRenderPower.Instance hudRender)) {
				return;
			}

			Context context = instance.createHolderContext();
			ListIterator<HudElement> listIterator = hudRender.getHudElements().listIterator();

			while (listIterator.hasNext()) {

				Context hudContext = context.forChild(".hud_elements[" + listIterator.nextIndex() + "]");
				HudElement hudElement = listIterator.next();

				if (dontHide(context, hudElement) && hudElement.shouldRender(hudContext, renderPhase)) {
					adder.accept(hudContext, hudElement);
				}

			}

		};

		prepare.accept(preparer);

	}

	private static void prepareCooldownElements(Consumer<Consumer<Power.Instance<?>>> prepare, HudRenderPhase renderPhase, BiConsumer<Context, HudElement> adder) {

		Consumer<Power.Instance<?>> preparer = instance -> {

			if (!(instance instanceof CooldownPower.Instance cooldown)) {
				return;
			}

			Context hudContext = instance.createHolderContext().forChild(".hud_element");
			HudElement hudElement = cooldown.getHudElement();

			if (dontHide(hudContext, hudElement) && cooldown.shouldRender(hudContext, renderPhase)) {
				adder.accept(hudContext, hudElement);
			}

		};

		prepare.accept(preparer);

	}

	private static void clearEntityTypeCache() {

		for (var entityType : BuiltInRegistries.ENTITY_TYPE) {

			if (entityType instanceof EntityCache entityCache) {
				entityCache.neo_apoli$setEntity(null);
			}

		}

	}

	private static boolean dontHide(Context context, HudElement element) {
		return !Minecraft.getInstance().options.hideGui
			|| !element.hideWithHud(context);
	}

}
