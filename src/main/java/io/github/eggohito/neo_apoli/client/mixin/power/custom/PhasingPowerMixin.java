package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.PhasingPower;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.SavedBlockPosition;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;

public abstract class PhasingPowerMixin {

	@Mixin(LevelRenderer.class)
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

		@ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;computeFogColor(Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IF)Lorg/joml/Vector4f;"))
		private Vector4f modifyFogColor(Vector4f original, GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera) {

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

		@WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;Lorg/joml/Vector4f;FZF)Lnet/minecraft/client/renderer/FogParameters;"))
		private FogParameters modifyFogData(Camera camera, FogRenderer.FogMode fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickProgress, Operation<FogParameters> original) {

			if (camera.getEntity() instanceof LivingEntity entity) {

				SavedBlockPosition viewBlocking = MiscUtil.getViewBlocking(entity);

				if (viewBlocking != null) {

					Context context = this.neo_apoli$getOrCreatePhasingContext(entity, viewBlocking);
					float viewDistanceCopy = viewDistance;

					viewDistance = PhasingPower.getViewDistanceOrElse(context, () -> viewDistanceCopy);

				}

			}

			return original.call(camera, fogType, color, viewDistance, thickenFog, tickProgress);

		}

		@WrapWithCondition(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addSkyPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/FogParameters;)V"))
		private boolean skipRenderingSkyWhenBlindnessPhasing(LevelRenderer renderer, FrameGraphBuilder frameGraphBuilder, Camera camera, float tickProgress, FogParameters fog) {
			return !neo_apoli$shouldApplyBlindnessEffects(camera);
		}

		@ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCloudsType()Lnet/minecraft/client/CloudStatus;"))
		private CloudStatus skipRenderingCloudWhenBlindnessPhasing(CloudStatus original, GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera) {

			if (neo_apoli$shouldApplyBlindnessEffects(camera)) {
				return CloudStatus.OFF;
			}

			else {
				return original;
			}

		}

		@Unique
		private static boolean neo_apoli$shouldApplyBlindnessEffects(Camera camera) {
			return camera.getEntity() instanceof LivingEntity livingEntity
				&& MiscUtil.getViewBlocking(livingEntity) != null
				&& PowersComponent.hasInstances(livingEntity, PhasingPower.Instance.class, instance -> instance.getRenderType() == PhasingPower.RenderType.BLINDNESS);
		}

	}

	@Mixin(ScreenEffectRenderer.class)
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

		@WrapWithCondition(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;renderTex(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"))
		private static boolean preventOverlayWhenPhasing(TextureAtlasSprite sprite, PoseStack matrices, MultiBufferSource vertexConsumers, @Local Player player) {

			SavedBlockPosition viewBlocking = MiscUtil.getViewBlocking(player);

			if (viewBlocking != null) {

				Context context = neo_apoli$getOrCreatePhasingContext(player, viewBlocking);
				boolean result = !PowersComponent.hasInstances(player, PhasingPower.Instance.class, instance -> instance.isActive(context));

				neo_apoli$phasingContext.clear();
				return result;

			}

			else {
				return true;
			}

		}

	}

}
