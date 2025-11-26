package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyModelColorOtherPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

public abstract class ModifyModelColorOtherPowerMixin {

	@Mixin(value = LivingEntityRenderer.class, priority = 1001)
	public static abstract class EntityModelApplier<S extends LivingEntityRenderState> {

		@Shadow
		public abstract ResourceLocation getTextureLocation(S state);

		@WrapOperation(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
		private void impl(EntityModel<S> model, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original, S methodRenderState, PoseStack methodPoseStack, MultiBufferSource methodBufferSource, int methodLight) {

			Minecraft client = Minecraft.getInstance();
			LocalPlayer viewer = client.player;

			if (methodRenderState instanceof EntityRenderCache renderCache && viewer != null) {

				List<ModifyModelColorOtherPower.Instance> instances = PowersComponent.getInstances(viewer, ModifyModelColorOtherPower.Instance.class);
				Context context = ModifyModelColorOtherPower.createContext(viewer, renderCache.neo_apoli$getEntity());

				if (!instances.isEmpty()) {

					color = ModifyModelColorOtherPower.modify(context, instances, color);
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

}
