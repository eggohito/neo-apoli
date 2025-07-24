package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyInvisibilityPower;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;

@Mixin(LivingEntityRenderer.class)
public abstract class ModifyInvisibilityPowerMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> {

	protected ModifyInvisibilityPowerMixin(EntityRendererFactory.Context context) {
		super(context);
	}

	@Unique
	protected WeakReference<Context> neo_apoli$invisibilityContext;

	@Unique
	protected Context neo_apoli$getOrCreateInvisibilityContext(Entity renderedEntity) {

		if (neo_apoli$invisibilityContext == null || neo_apoli$invisibilityContext.get() == null) {

			Context context = Context.builder(PowerTypes.MODIFY_INVISIBILITY.contextType())
				.addNullable(ContextParameters.ACTOR, MinecraftClient.getInstance().player)
				.add(ContextParameters.TARGET, renderedEntity)
				.add(ContextParameters.ENTITY, renderedEntity)
				.add(ContextParameters.ENTITY_POS, renderedEntity.getPos())
				.build(renderedEntity.getWorld());

			this.neo_apoli$invisibilityContext = new WeakReference<>(context);

		}

		return neo_apoli$invisibilityContext.get();

	}

	@ModifyExpressionValue(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"))
	private boolean showOrHideOutlineWhenInvisible(boolean original, LivingEntity renderedEntity) {

		if (original) {

			Context context = this.neo_apoli$getOrCreateInvisibilityContext(renderedEntity);
			boolean result = PowersComponent.hasPowerImpl(renderedEntity, ModifyInvisibilityPower.Impl.class, impl -> impl.shouldRenderOutline(context));

			this.neo_apoli$invisibilityContext.clear();
			return result;

		}

		else {
			return false;
		}

	}

	@WrapWithCondition(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/EntityRenderState;FF)V"))
	private boolean showOrHideArmorFeatureWhenInvisible(FeatureRenderer<S, M> featureRenderer, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, EntityRenderState state, float limbAngle, float limbDistance) {

		if (state instanceof EntityRenderCache renderCache && renderCache.neo_apoli$getEntity() != null && featureRenderer instanceof ArmorFeatureRenderer<?, ?, ?>) {

			Context context = this.neo_apoli$getOrCreateInvisibilityContext(renderCache.neo_apoli$getEntity());
			boolean result = PowersComponent.hasPowerImpl(renderCache.neo_apoli$getEntity(), ModifyInvisibilityPower.Impl.class, impl -> impl.shouldRenderArmor(context));

			this.neo_apoli$invisibilityContext.clear();
			return result;

		}

		else {
			return true;
		}

	}

}
