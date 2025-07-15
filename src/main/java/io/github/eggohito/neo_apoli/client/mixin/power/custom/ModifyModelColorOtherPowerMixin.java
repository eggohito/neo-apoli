package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyModelColorOtherPower;
import io.github.eggohito.neo_apoli.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

public abstract class ModifyModelColorOtherPowerMixin {

	@Mixin(value = LivingEntityRenderer.class, priority = 1001)
	public static abstract class EntityModelApplier {

		@WrapOperation(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
		private <S extends LivingEntityRenderState> void impl(EntityModel<S> model, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int argb, Operation<Void> original, S renderState) {

			MinecraftClient client = MinecraftClient.getInstance();
			ClientPlayerEntity viewer = client.player;

			if (renderState instanceof EntityRenderCache renderCache && viewer != null) {
				argb = PowersComponent.getPowerImpls(viewer, ModifyModelColorOtherPower.Impl.class, impl -> true)
					.stream()
					.map(impl -> impl.getColor(renderCache.neo_apoli$getEntity()))
					.flatMap(Optional::stream)
					.reduce(Color.fromArgb(argb), Color::mix)
					.toArgb();
			}

			original.call(model, matrixStack, vertexConsumer, light, overlay, argb);

		}

	}

}
