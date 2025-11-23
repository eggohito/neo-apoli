package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.client.duck.PlayerRendererHelper;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyModelColorSelfPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
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
		public abstract Identifier getTexture(S state);

		@WrapOperation(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
		private void impl(EntityModel<S> model, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original, S renderState, MatrixStack methodMatrixStack, VertexConsumerProvider methodVertexConsumerProvider, int methodLight) {

			MinecraftClient client = MinecraftClient.getInstance();
			ClientPlayerEntity viewer = client.player;

			if (renderState instanceof EntityRenderCache renderCache && renderCache.neo_apoli$getEntity() != null) {

				List<ModifyModelColorSelfPower.Instance> instances = PowersComponent.getInstances(renderCache.neo_apoli$getEntity(), ModifyModelColorSelfPower.Instance.class);
				Context context = ModifyModelColorSelfPower.createContext(renderCache.neo_apoli$getEntity(), viewer);

				if (!instances.isEmpty()) {

					color = ModifyModelColorSelfPower.modify(context, instances, color);

					renderCache.neo_apoli$setColor(color);
					float alpha = ColorHelper.getAlphaFloat(color);

					if (alpha < 1.0F) {
						vertexConsumer = methodVertexConsumerProvider.getBuffer(RenderLayer.getItemEntityTranslucentCull(this.getTexture(renderState)));
					}

				}

			}

			original.call(model, matrixStack, vertexConsumer, light, overlay, color);

		}

	}

	@Mixin(PlayerEntityRenderer.class)
	public static abstract class PlayerArmPartApplier implements PlayerRendererHelper {

		@Unique
		protected WeakReference<PlayerEntity> neo_apoli$player = new WeakReference<>(null);

		@Override
		public PlayerEntity neo_apoli$getPlayer() {
			return this.neo_apoli$player.get();
		}

		@Override
		public void neo_apoli$setPlayer(@Nullable PlayerEntity player) {

			if (player == null) {
				this.neo_apoli$player.clear();
			}

			else {
				this.neo_apoli$player = new WeakReference<>(player);
			}

		}

		@WrapOperation(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
		private void impl(ModelPart armPart, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, Operation<Void> original) {

			PlayerEntity player = this.neo_apoli$getPlayer();
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

	@Mixin(HeldItemRenderer.class)
	public static abstract class PlayerCache {

		@Shadow
		@Final
		private EntityRenderDispatcher entityRenderDispatcher;

		@Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
		private void onFirstPersonRender(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

			if (this.entityRenderDispatcher.getRenderer(player) instanceof PlayerRendererHelper helper) {
				helper.neo_apoli$setPlayer(player);
			}

		}

		@Inject(method = "renderFirstPersonItem", at = @At("TAIL"))
		private void cleanUpAfter(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

			if (this.entityRenderDispatcher.getRenderer(player) instanceof PlayerRendererHelper rendererHelper) {
				rendererHelper.neo_apoli$setPlayer(null);
			}

		}

	}

}
