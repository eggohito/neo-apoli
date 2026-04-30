package io.github.eggohito.neo_apoli.client.mixin.impl.power.custom.modify_model_color_self;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.eggohito.neo_apoli.power.custom.ModifyModelColorSelfPower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<S extends LivingEntityRenderState> {

	@Shadow
	public abstract ResourceLocation getTextureLocation(S state);

	@WrapOperation(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
	void modifyModelColor(EntityModel<S> model, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original, S methodRenderState, PoseStack methodPoseStack, MultiBufferSource methodBufferSource, int methodLight) {

		modifyColor:
		{

			Entity viewer = Minecraft.getInstance().getCameraEntity();
			Entity rendered = methodRenderState.neo_apoli$getEntity();

			if (viewer == null || rendered == null) {
				break modifyColor;
			}

			int originalColor = color;
			color = ModifyModelColorSelfPower.modify(viewer, rendered, color);

			if (originalColor != color) {

				methodRenderState.neo_apoli$setColor(color);

				if (ARGB.alphaFloat(color) < 1.0F) {
					vertexConsumer = methodBufferSource.getBuffer(RenderType.itemEntityTranslucentCull(this.getTextureLocation(methodRenderState)));
				}

			}

		}

		original.call(model, poseStack, vertexConsumer, light, overlay, color);

	}

}
