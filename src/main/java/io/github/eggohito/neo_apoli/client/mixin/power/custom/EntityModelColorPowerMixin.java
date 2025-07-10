package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.EntityModelColorPower;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(LivingEntityRenderer.class)
public abstract class EntityModelColorPowerMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

	@WrapOperation(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
	private void test(EntityModel<S> model, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original, S renderState) {

		if (!(renderState instanceof EntityRenderCache renderCache) || renderCache.neo_apoli$getEntity() == null) {
			original.call(model, matrixStack, vertexConsumer, light, overlay, color);
		}

		else {

			//	TODO: Surely there's a better way to do this apart from having to stream the list multiple times
			Entity entity = renderCache.neo_apoli$getEntity();
			List<EntityModelColorPower.Impl> impls = PowersComponent.getPowerImpls(entity, EntityModelColorPower.Impl.class, EntityModelColorPower.Impl::isActive);

			float oldAlpha = ColorHelper.getAlphaFloat(color);
			float oldRed = ColorHelper.getRedFloat(color);
			float oldGreen = ColorHelper.getGreenFloat(color);
			float oldBlue = ColorHelper.getBlueFloat(color);

			float newAlpha = impls
				.stream()
				.map(EntityModelColorPower.Impl::getAlpha)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.min(Float::compareTo)
				.map(factor -> oldAlpha * factor)
				.orElse(oldAlpha);
			float newRed = impls
				.stream()
				.map(EntityModelColorPower.Impl::getRed)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.reduce(oldRed, (a, b) -> a * b);
			float newGreen = impls
				.stream()
				.map(EntityModelColorPower.Impl::getGreen)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.reduce(oldGreen, (a, b) -> a * b);
			float newBlue = impls
				.stream()
				.map(EntityModelColorPower.Impl::getBlue)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.reduce(oldBlue, (a, b) -> a * b);

			original.call(model, matrixStack, vertexConsumer, light, overlay, ColorHelper.fromFloats(newAlpha, newRed, newGreen, newBlue));

		}

	}

}
