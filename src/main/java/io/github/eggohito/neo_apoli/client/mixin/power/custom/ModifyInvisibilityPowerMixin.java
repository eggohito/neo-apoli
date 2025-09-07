package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyInvisibilityPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class ModifyInvisibilityPowerMixin {

	@Mixin(LivingEntityRenderer.class)
	public abstract static class OutlineAndArmorProxy<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> {

		protected OutlineAndArmorProxy(EntityRendererFactory.Context context) {
			super(context);
		}

		@Unique
		protected WeakReference<Context> neo_apoli$invisibilityContext;

		@Unique
		protected Context neo_apoli$getOrCreateInvisibilityContext(@NotNull Entity renderedEntity) {

			Context context = Optional.ofNullable(this.neo_apoli$invisibilityContext)
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyInvisibilityPower.createContext(renderedEntity, MinecraftClient.getInstance().player));

			this.neo_apoli$invisibilityContext = new WeakReference<>(context);
			return context;

		}

		@ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"))
		private boolean showOrHideOutlineWhenInvisible(boolean original, LivingEntity renderedEntity) {

			if (original) {

				Context context = this.neo_apoli$getOrCreateInvisibilityContext(renderedEntity);
				boolean result = !PowersComponent.hasInstances(renderedEntity, ModifyInvisibilityPower.Instance.class, Predicate.not(instance -> instance.shouldRenderOutline(context)));

				this.neo_apoli$invisibilityContext.clear();
				return result;

			}

			else {
				return false;
			}

		}

		@WrapWithCondition(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/EntityRenderState;FF)V"))
		private boolean showOrHideArmorFeatureWhenInvisible(FeatureRenderer<S, M> featureRenderer, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, EntityRenderState state, float limbAngle, float limbDistance) {

			if (featureRenderer instanceof ArmorFeatureRenderer<?, ?, ?> && state instanceof EntityRenderCache renderCache && renderCache.neo_apoli$getEntity() != null) {

				Context context = this.neo_apoli$getOrCreateInvisibilityContext(renderCache.neo_apoli$getEntity());
				boolean result = !PowersComponent.hasInstances(renderCache.neo_apoli$getEntity(), ModifyInvisibilityPower.Instance.class, Predicate.not(instance -> instance.shouldRenderArmor(context)));

				this.neo_apoli$invisibilityContext.clear();
				return result;

			}

			else {
				return true;
			}

		}

	}

}
