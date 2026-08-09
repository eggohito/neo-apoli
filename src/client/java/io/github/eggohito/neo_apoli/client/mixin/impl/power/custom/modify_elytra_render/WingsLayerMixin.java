package io.github.eggohito.neo_apoli.client.mixin.impl.power.custom.modify_elytra_render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.eggohito.neo_apoli.power.custom.ModifyElytraRenderPower;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WingsLayer.class)
public abstract class WingsLayerMixin {

	@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/WingsLayer;getPlayerElytraTexture(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)Lnet/minecraft/resources/ResourceLocation;"), cancellable = true)
	private <S extends HumanoidRenderState> void skipWingsRenderIfHasCustomElytra(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, S renderState, float yRot, float xRot, CallbackInfo ci) {

		//	Disable the vanilla elytra renderer if the rendered entity has the `modify/elytra/render` power type
		//	(since it has its own renderer)
		if (Powers.hasInstances(renderState.neo_apoli$getEntity(), ModifyElytraRenderPower.Instance.class)) {
			ci.cancel();
		}

	}

}
