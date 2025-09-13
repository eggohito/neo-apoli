package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.PhasingPower;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.SavedBlockPosition;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;

public abstract class PhasingPowerMixin {

	@Mixin(WorldRenderer.class)
	public abstract static class ModifyFogDistanceAndColor {

		@Unique
		private WeakReference<Context> neo_apoli$phasingContext;

		@Unique
		private Context neo_apoli$getOrCreatePhasingContext(Entity entity, SavedBlockPosition savedBlock) {

			Context context = Optional.ofNullable(this.neo_apoli$phasingContext)
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> PhasingPower.createContext(entity, savedBlock));

			this.neo_apoli$phasingContext = new WeakReference<>(context);
			return context;

		}

		@ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;getFogColor(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)Lorg/joml/Vector4f;"))
		private Vector4f modifyFogColor(Vector4f original, ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera) {

			if (neo_apoli$shouldApplyBlindnessEffects(camera)) {
				return original
					.setComponent(0, original.get(0) * 0.1F)
					.setComponent(1, original.get(1) * 0.1F)
					.setComponent(2, original.get(2) * 0.1F);
			}

			else {
				return original;
			}

		}

		@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;Lorg/joml/Vector4f;FZF)Lnet/minecraft/client/render/Fog;"))
		private Fog modifyFogData(Camera camera, BackgroundRenderer.FogType fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickProgress, Operation<Fog> original) {

			if (camera.getFocusedEntity() instanceof LivingEntity entity) {

				SavedBlockPosition inWallBlock = MiscUtil.getInWallBlock(entity);

				if (inWallBlock != null) {

					Context context = this.neo_apoli$getOrCreatePhasingContext(entity, inWallBlock);

					viewDistance = PowersComponent.getInstances(entity, PhasingPower.Instance.class, instance -> instance.getRenderType() == PhasingPower.RenderType.BLINDNESS)
						.stream()
						.filter(instance -> instance.doesApply(context))
						.map(PhasingPower.Instance::getViewDistance)
						.min(Float::compareTo)
						.orElse(viewDistance);

				}

			}

			return original.call(camera, fogType, color, viewDistance, thickenFog, tickProgress);

		}

		@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderSky(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/render/Fog;)V"))
		private boolean skipRenderingSkyWhenBlindnessPhasing(WorldRenderer renderer, FrameGraphBuilder frameGraphBuilder, Camera camera, float tickProgress, Fog fog) {
			return !neo_apoli$shouldApplyBlindnessEffects(camera);
		}

		@ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getCloudRenderModeValue()Lnet/minecraft/client/option/CloudRenderMode;"))
		private CloudRenderMode skipRenderingCloudWhenBlindnessPhasing(CloudRenderMode original, ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera) {

			if (neo_apoli$shouldApplyBlindnessEffects(camera)) {
				return CloudRenderMode.OFF;
			}

			else {
				return original;
			}

		}

		@Unique
		private static boolean neo_apoli$shouldApplyBlindnessEffects(Camera camera) {
			return camera.getFocusedEntity() instanceof LivingEntity livingEntity
				&& MiscUtil.getInWallBlock(livingEntity) != null
				&& PowersComponent.hasInstances(livingEntity, PhasingPower.Instance.class, instance -> instance.getRenderType() == PhasingPower.RenderType.BLINDNESS);
		}

	}

	@Mixin(InGameOverlayRenderer.class)
	public static abstract class PreventBlockOverlay {

		@Unique
		private static WeakReference<Context> neo_apoli$phasingContext;

		@Unique
		private static Context neo_apoli$getOrCreatePhasingContext(Entity entity, SavedBlockPosition savedBlock) {

			Context context = Optional.ofNullable(neo_apoli$phasingContext)
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> PhasingPower.createContext(entity, savedBlock));

			neo_apoli$phasingContext = new WeakReference<>(context);
			return context;

		}

		@WrapWithCondition(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameOverlayRenderer;renderInWallOverlay(Lnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"))
		private static boolean preventOverlayWhenPhasing(Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Local PlayerEntity player) {

			SavedBlockPosition inWallBlock = MiscUtil.getInWallBlock(player);

			if (inWallBlock != null) {

				Context context = neo_apoli$getOrCreatePhasingContext(player, inWallBlock);
				boolean result = !PowersComponent.hasInstances(player, PhasingPower.Instance.class, instance -> instance.doesApply(context));

				neo_apoli$phasingContext.clear();
				return result;

			}

			else {
				return true;
			}

		}

	}

}
