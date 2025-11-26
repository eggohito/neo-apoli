package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.client.duck.PlayerRendererHelper;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyModelColorSelfPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class ModifyModelColorSelfPowerMixin {

	@Mixin(LivingEntityRenderer.class)
	public static abstract class EntityModelApplier<S extends LivingEntityRenderState> {

		@Shadow
		public abstract ResourceLocation getTextureLocation(S state);

		@WrapOperation(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
		private void impl(EntityModel<S> model, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original, S methodRenderState, PoseStack methodPoseStack, MultiBufferSource methodBufferSource, int methodLight) {

			Minecraft client = Minecraft.getInstance();
			LocalPlayer viewer = client.player;

			if (methodRenderState instanceof EntityRenderCache renderCache && renderCache.neo_apoli$getEntity() != null) {

				List<ModifyModelColorSelfPower.Instance> instances = PowersComponent.getInstances(renderCache.neo_apoli$getEntity(), ModifyModelColorSelfPower.Instance.class);
				Context context = ModifyModelColorSelfPower.createContext(renderCache.neo_apoli$getEntity(), viewer);

				if (!instances.isEmpty()) {

					color = ModifyModelColorSelfPower.modify(context, instances, color);
					float alpha = ARGB.alphaFloat(color);

					renderCache.neo_apoli$setColor(color);

					if (alpha < 1.0F) {
						vertexConsumer = methodBufferSource.getBuffer(RenderType.itemEntityTranslucentCull(this.getTextureLocation(methodRenderState)));
					}

				}

			}

			original.call(model, poseStack, vertexConsumer, light, overlay, color);

		}

	}

	@Mixin(PlayerRenderer.class)
	public static abstract class PlayerArmPartApplier implements PlayerRendererHelper {

		@Unique
		protected WeakReference<Player> neo_apoli$player = new WeakReference<>(null);

		@Override
		public Player neo_apoli$getPlayer() {
			return this.neo_apoli$player.get();
		}

		@Override
		public void neo_apoli$setPlayer(@Nullable Player player) {

			if (player == null) {
				this.neo_apoli$player.clear();
			}

			else {
				this.neo_apoli$player = new WeakReference<>(player);
			}

		}

		@WrapOperation(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
		private void impl(ModelPart armPart, PoseStack matrices, VertexConsumer vertices, int light, int overlay, Operation<Void> original) {

			Player player = this.neo_apoli$getPlayer();
			List<ModifyModelColorSelfPower.Instance> instances = PowersComponent.getInstances(player, ModifyModelColorSelfPower.Instance.class);

			if (player != null && !instances.isEmpty()) {

				Context context = ModifyModelColorSelfPower.createContext(player, null);
				int color = ModifyModelColorSelfPower.modify(context, instances, -1);

				armPart.render(matrices, vertices, light, overlay, color);

			}

			else {
				original.call(armPart, matrices, vertices, light, overlay);
			}

		}

	}

	@Mixin(ItemInHandRenderer.class)
	public static abstract class PlayerCache {

		@Shadow
		@Final
		private EntityRenderDispatcher entityRenderDispatcher;

		@Inject(method = "renderArmWithItem", at = @At("HEAD"))
		private void onFirstPersonRender(AbstractClientPlayer player, float tickProgress, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {

			if (this.entityRenderDispatcher.getRenderer(player) instanceof PlayerRendererHelper helper) {
				helper.neo_apoli$setPlayer(player);
			}

		}

		@Inject(method = "renderArmWithItem", at = @At("TAIL"))
		private void cleanUpAfter(AbstractClientPlayer player, float tickProgress, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {

			if (this.entityRenderDispatcher.getRenderer(player) instanceof PlayerRendererHelper rendererHelper) {
				rendererHelper.neo_apoli$setPlayer(null);
			}

		}

	}

}
