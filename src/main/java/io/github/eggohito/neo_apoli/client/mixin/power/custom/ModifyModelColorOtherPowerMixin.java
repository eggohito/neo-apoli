package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyModelColorOtherPower;
import io.github.eggohito.neo_apoli.util.color.Argb;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

public abstract class ModifyModelColorOtherPowerMixin {

	@Mixin(value = LivingEntityRenderer.class, priority = 1001)
	public static abstract class EntityModelApplier<S extends LivingEntityRenderState> {

		@Shadow
		public abstract Identifier getTexture(S state);

		@WrapOperation(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
		private void impl(EntityModel<S> model, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int packedArgb, Operation<Void> original, S renderState, MatrixStack methodMatrixStack, VertexConsumerProvider methodVertexConsumerProvider, int methodLight) {

			MinecraftClient client = MinecraftClient.getInstance();
			ClientPlayerEntity viewer = client.player;

			if (renderState instanceof EntityRenderCache renderCache && viewer != null) {

				List<ModifyModelColorOtherPower.Impl> impls = PowersComponent.getPowerImpls(viewer, ModifyModelColorOtherPower.Impl.class);

				if (!impls.isEmpty()) {

					Argb unpackedArgb = impls
						.stream()
						.map(impl -> impl.getColorForEntity(renderCache.neo_apoli$getEntity()))
						.flatMap(Optional::stream)
						.reduce(Argb.unpack(packedArgb), Argb::mix);

					packedArgb = unpackedArgb.pack();
					renderCache.neo_apoli$setColor(unpackedArgb);

					if (unpackedArgb.alpha() < 1.0F) {
						vertexConsumer = methodVertexConsumerProvider.getBuffer(RenderLayer.getItemEntityTranslucentCull(this.getTexture(renderState)));
					}

				}

			}

			original.call(model, matrixStack, vertexConsumer, light, overlay, packedArgb);

		}

	}

}
