package io.github.eggohito.neo_apoli.client.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

	@ModifyExpressionValue(method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;getShadowOpacity(Lnet/minecraft/client/render/entity/state/EntityRenderState;)F"))
	private <S extends EntityRenderState> float blendCustomAlphaAndShadowOpacity(float original, S state) {

		if (state instanceof EntityRenderCache renderCache && renderCache.neo_apoli$getColor() != -1) {
			return original * ColorHelper.getAlphaFloat(renderCache.neo_apoli$getColor());
		}

		else {
			return original;
		}

	}

}
