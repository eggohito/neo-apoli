package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyInvisibilityPower;
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
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class ModifyInvisibilityPowerMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> {

	protected ModifyInvisibilityPowerMixin(EntityRendererFactory.Context context) {
		super(context);
	}

	@ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"))
	private boolean showOrHideOutlineWhenInvisible(boolean original, LivingEntity entity) {
		return original
			&& !PowersComponent.hasPowerImpl(entity, ModifyInvisibilityPower.Impl.class, impl -> !impl.shouldRenderOutline() && impl.isActive());
	}

	@WrapWithCondition(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/EntityRenderState;FF)V"))
	private boolean showOrHideArmorFeatureWhenInvisible(FeatureRenderer<S, M> featureRenderer, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, EntityRenderState state, float limbAngle, float limbDistance) {

		if (state instanceof EntityRenderCache renderCache && renderCache.neo_apoli$getEntity() != null && featureRenderer instanceof ArmorFeatureRenderer<?,?,?>) {
			return !PowersComponent.hasPowerImpl(renderCache.neo_apoli$getEntity(), ModifyInvisibilityPower.Impl.class, impl -> !impl.shouldRenderArmor() && impl.isActive());
		}

		else {
			return true;
		}

	}

}
