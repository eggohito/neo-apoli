package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.custom.ModifyInvisibilityPower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiPredicate;

public abstract class ModifyInvisibilityPowerMixin {

	@Mixin(LivingEntityRenderer.class)
	public abstract static class OutlineAndArmorProxy<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> {

		protected OutlineAndArmorProxy(EntityRendererProvider.Context context) {
			super(context);
		}

		@ModifyExpressionValue(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z"))
		private boolean showOrHideOutlineWhenInvisible(boolean original, LivingEntity renderedEntity) {

			try {

				if (original) {
					BiPredicate<ModifyInvisibilityPower.Instance, Context> renderOutline = ModifyInvisibilityPower.Instance::isActiveAndShouldRenderOutline;
					return !ModifyInvisibilityPower.modify(renderedEntity, Minecraft.getInstance().getCameraEntity(), renderOutline.negate(), () -> false);
				}

				else {
					return false;
				}

			}

			finally {
				ModifyInvisibilityPower.VISITOR.clear();
			}

		}

		@WrapWithCondition(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V"))
		private boolean showOrHideArmorFeatureWhenInvisible(RenderLayer<S, M> featureRenderer, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, EntityRenderState state, float limbAngle, float limbDistance) {

			try {

				if (featureRenderer instanceof HumanoidArmorLayer) {
					BiPredicate<ModifyInvisibilityPower.Instance, Context> renderArmor = ModifyInvisibilityPower.Instance::isActiveAndShouldRenderArmor;
					return ModifyInvisibilityPower.modify(state.neo_apoli$getEntity(), Minecraft.getInstance().getCameraEntity(), renderArmor.negate(), () -> true);
				}

				else {
					return true;
				}

			}

			finally {
				ModifyInvisibilityPower.VISITOR.clear();
			}

		}

	}

}
