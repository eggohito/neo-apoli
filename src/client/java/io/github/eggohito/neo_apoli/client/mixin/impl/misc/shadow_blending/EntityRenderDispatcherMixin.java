package io.github.eggohito.neo_apoli.client.mixin.impl.misc.shadow_blending;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

	@ModifyExpressionValue(method = "render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;getShadowStrength(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)F"))
	private <S extends EntityRenderState> float blendCustomAlphaAndShadowOpacity(float original, S state) {

		if (state.neo_apoli$getColor() != -1) {
			return original * ARGB.alphaFloat(state.neo_apoli$getColor());
		}

		else {
			return original;
		}

	}

}
