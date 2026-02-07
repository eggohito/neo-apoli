package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.PhasingPower;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

//	FIXME: Modifying fog color/distance with this method doesn't work with shaders
public abstract class PhasingPowerMixin {

	@Mixin(LevelRenderer.class)
	public abstract static class ModifyFogData {

		@ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;computeFogColor(Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IF)Lorg/joml/Vector4f;"))
		private Vector4f modifyColor(Vector4f original, GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, @Local(ordinal = 0) float partialTick) {

			if (neo_apoli$shouldApplyBlindnessEffects(camera)) {
				return original.lerp(original.mul(0.1F, 0.1F, 0.1F, 1.0F), partialTick);
			}

			else {
				return original;
			}

		}

		@WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;Lorg/joml/Vector4f;FZF)Lnet/minecraft/client/renderer/FogParameters;"))
		private FogParameters modifyDistance(Camera camera, FogRenderer.FogMode fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickProgress, Operation<FogParameters> original) {

			try {

				Entity entity = camera.getEntity();
				CachedBlock viewBlocking = MiscUtil.getViewBlocking(entity);

				if (viewBlocking != null) {
					viewDistance = PhasingPower.modifyViewDistance(entity, viewBlocking, viewDistance);
				}

				return original.call(camera, fogType, color, viewDistance, thickenFog, tickProgress);

			}

			finally {
				PhasingPower.VISITOR.clear();
			}

		}

		@WrapWithCondition(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addSkyPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/FogParameters;)V"))
		private boolean skipRenderingSkyWhenBlindnessPhasing(LevelRenderer renderer, FrameGraphBuilder frameGraphBuilder, Camera camera, float tickProgress, FogParameters fog) {
			return !neo_apoli$shouldApplyBlindnessEffects(camera);
		}

		@WrapWithCondition(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addCloudsPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/CloudStatus;Lnet/minecraft/world/phys/Vec3;FIF)V"))
		private boolean skipRenderingCloudsWhenBlindessPhasing(LevelRenderer renderer, FrameGraphBuilder frameGraphBuilder, CloudStatus cloudStatus, Vec3 cameraPosition, float ticks, int cloudColor, float cloudHeight, @Local(argsOnly = true) Camera camera) {
			return !neo_apoli$shouldApplyBlindnessEffects(camera);
		}

		@Unique
		private static boolean neo_apoli$shouldApplyBlindnessEffects(Camera camera) {
			return neo_apoli$shouldApplyBlindnessEffects(camera.getEntity());
		}

		@Unique
		private static boolean neo_apoli$shouldApplyBlindnessEffects(Entity entity) {
			return MiscUtil.getViewBlocking(entity) != null
				&& PowersComponent.hasInstances(entity, PhasingPower.Instance.class, instance -> instance.getRenderType() == PhasingPower.RenderType.BLINDNESS);
		}

	}

	@Mixin(ScreenEffectRenderer.class)
	public static abstract class PreventBlockOverlay {

		@ModifyExpressionValue(method = "renderScreenEffect", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;noPhysics:Z", opcode = Opcodes.GETFIELD))
		private static boolean noPhysicsProxy(boolean original, @Local Player player) {

			try {
				CachedBlock viewBlocking = MiscUtil.getViewBlocking(player);
				return original
					|| (viewBlocking != null && PhasingPower.doesApply(player, viewBlocking, Power.Instance::isActive));
			}

			finally {
				PhasingPower.VISITOR.clear();
			}

		}

	}

}
