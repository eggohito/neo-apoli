package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.client.duck.PlayerRendererHelper;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyModelColorSelfPower;
import io.github.eggohito.neo_apoli.util.Color;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.Optional;

public abstract class ModifyModelColorSelfMixin {

	@Mixin(LivingEntityRenderer.class)
	public static abstract class EntityModelApplier {

		@WrapOperation(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
		private <S extends LivingEntityRenderState> void impl(EntityModel<S> model, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int argb, Operation<Void> original, S renderState) {

			if (renderState instanceof EntityRenderCache renderCache && renderCache.neo_apoli$getEntity() != null) {
				argb = PowersComponent.getPowerImpls(renderCache.neo_apoli$getEntity(), ModifyModelColorSelfPower.Impl.class, impl -> true)
					.stream()
					.map(ModifyModelColorSelfPower.Impl::getColor)
					.flatMap(Optional::stream)
					.reduce(Color.fromArgb(argb), Color::mix)
					.toArgb();
			}

			original.call(model, matrixStack, vertexConsumer, light, overlay, argb);

		}

	}

	@Mixin(PlayerEntityRenderer.class)
	public static abstract class PlayerArmPartApplier implements PlayerRendererHelper {

		@Unique
		private WeakReference<PlayerEntity> neo_apoli$player;

		@Override
		public PlayerEntity neo_apoli$getPlayer() {
			return neo_apoli$player.get();
		}

		@Override
		public void neo_apoli$setPlayer(@Nullable PlayerEntity player) {
			this.neo_apoli$player = new WeakReference<>(player);
		}

		@WrapOperation(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"))
		private void impl(ModelPart armPart, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, Operation<Void> original) {

			if (this.neo_apoli$getPlayer() != null) {

				int argb = PowersComponent.getPowerImpls(this.neo_apoli$getPlayer(), ModifyModelColorSelfPower.Impl.class, impl -> true)
					.stream()
					.map(ModifyModelColorSelfPower.Impl::getColor)
					.flatMap(Optional::stream)
					.reduce(Color.DEFAULT, Color::mix)
					.toArgb();

				armPart.render(matrices, vertices, light, overlay, argb);

			}

			else {
				original.call(armPart, matrices, vertices, light, overlay);
			}

		}

	}

	@Mixin(HeldItemRenderer.class)
	public static abstract class PlayerCache {

		@Shadow
		@Final
		private EntityRenderDispatcher entityRenderDispatcher;

		@Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
		private void onFirstPersonRender(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

			if (this.entityRenderDispatcher.getRenderer(player) instanceof PlayerRendererHelper controller) {
				controller.neo_apoli$setPlayer(player);
			}

		}

		@Inject(method = "renderFirstPersonItem", at = @At("TAIL"))
		private void cleanUpAfter(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

			if (this.entityRenderDispatcher.getRenderer(player) instanceof PlayerRendererHelper controller) {
				controller.neo_apoli$setPlayer(null);
			}

		}

	}

}
