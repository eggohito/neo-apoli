package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.power.custom.ModifyInvisibilityPower;
import io.github.eggohito.neo_apoli.util.context.Context;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;

public abstract class ModifyInvisibilityPowerMixin {

	@Mixin(LivingEntityRenderer.class)
	public abstract static class OutlineAndArmorProxy<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> {

		protected OutlineAndArmorProxy(EntityRendererProvider.Context context) {
			super(context);
		}

		@Unique
		protected WeakReference<Context> neo_apoli$invisibilityContext;

		@Unique
		protected Context neo_apoli$getOrCreateInvisibilityContext(@NotNull Entity renderedEntity) {

			Context context = Optional.ofNullable(this.neo_apoli$invisibilityContext)
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyInvisibilityPower.createContext(renderedEntity, Minecraft.getInstance().player));

			this.neo_apoli$invisibilityContext = new WeakReference<>(context);
			return context;

		}

		@ModifyExpressionValue(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z"))
		private boolean showOrHideOutlineWhenInvisible(boolean original, LivingEntity renderedEntity) {

			if (original) {

				Context context = this.neo_apoli$getOrCreateInvisibilityContext(renderedEntity);
				boolean result = !ModifyInvisibilityPower.doesApply(context, (instance, ctx) -> !instance.shouldRenderOutline(ctx));

				this.neo_apoli$invisibilityContext.clear();
				return result;

			}

			else {
				return false;
			}

		}

		@WrapWithCondition(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V"))
		private boolean showOrHideArmorFeatureWhenInvisible(RenderLayer<S, M> featureRenderer, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, EntityRenderState state, float limbAngle, float limbDistance) {

			if (featureRenderer instanceof HumanoidArmorLayer<?, ?, ?> && state instanceof EntityRenderCache renderCache && renderCache.neo_apoli$getEntity() != null) {

				Context context = this.neo_apoli$getOrCreateInvisibilityContext(renderCache.neo_apoli$getEntity());
				boolean result = ModifyInvisibilityPower.doesApply(context, (instance, ctx) -> !instance.shouldRenderArmor(ctx));

				this.neo_apoli$invisibilityContext.clear();
				return result;

			}

			else {
				return true;
			}

		}

	}

}
