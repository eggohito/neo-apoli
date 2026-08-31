package io.github.eggohito.neo_apoli.client.integration;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.v0.hud.element.HudElement;
import io.github.eggohito.neo_apoli.client.api.v0.hud.HudElementHelper;
import io.github.eggohito.neo_apoli.client.renderer.entity.layers.PowerWingsLayer;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.impl.misc.CustomClearable;
import io.github.eggohito.neo_apoli.impl.misc.EntityCache;
import io.github.eggohito.neo_apoli.mixin.access.DefaultAttributesAccessor;
import io.github.eggohito.neo_apoli.power.custom.HudRenderPower;
import io.github.eggohito.neo_apoli.power.custom.misc.CooldownPower;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.ListIterator;

public class PowerClientIntegrations {

	public static void registerAll() {

		ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> {
			clearEntityTypeCache();
			clearAttributeEntityCache();
		});

		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, level) -> {
			clearEntityTypeCache();
			clearAttributeEntityCache();
		});

		HudElementHelper.registerSource(NeoApoli.id("cooldown"), PowerClientIntegrations::prepareCooldownElements);
		HudElementHelper.registerSource(NeoApoli.id("hud_render"), PowerClientIntegrations::prepareHudElements);

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

	private static List<HudElement.WithContext> prepareHudElements(Player viewer, HudElement.RenderPhase renderPhase) {

		List<HudElement.WithContext> result = new ObjectArrayList<>();

		for (var instance : Powers.getInstances(viewer, HudRenderPower.Instance.class)) {

			Context context = instance.createHolderContext(viewer);
			ListIterator<HudElement> listIterator = instance.hudElements().listIterator();

			while (listIterator.hasNext()) {

				Context hudContext = context.forChild(".hud_elements[" + listIterator.nextIndex() + "]");
				HudElement hudElement = listIterator.next();

				if (showWithHud(hudContext, hudElement) && hudElement.shouldRender(hudContext, renderPhase)) {
					result.add(new HudElement.WithContext(hudContext, hudElement));
				}

			}

		}

		return result;

	}

	private static List<HudElement.WithContext> prepareCooldownElements(Player viewer, HudElement.RenderPhase renderPhase) {

		List<HudElement.WithContext> result = new ObjectArrayList<>();

		for (var instance : Powers.getInstances(viewer, CooldownPower.Instance.class)) {

			Context hudContext = instance.createContext(viewer).forChild(".hud_element");
			HudElement hudElement = instance.hudElement();

			if (showWithHud(hudContext, hudElement) && instance.shouldRender(hudContext, renderPhase)) {
				result.add(new HudElement.WithContext(hudContext, hudElement));
			}

		}

		return result;

	}

	private static void clearEntityTypeCache() {

		for (var entityType : BuiltInRegistries.ENTITY_TYPE) {

			if (entityType instanceof EntityCache entityCache) {
				entityCache.neo_apoli$setEntity(null);
			}

		}

	}

	private static void clearAttributeEntityCache() {

		for (var supplier : DefaultAttributesAccessor.getSuppliers().values()) {

			if (supplier instanceof CustomClearable clearable) {
				clearable.neo_apoli$clear();
			}

		}

	}

	private static boolean showWithHud(Context context, HudElement element) {
		return !Minecraft.getInstance().options.hideGui || !element.hideWithHud(context);
	}

}
