package io.github.eggohito.neo_apoli.client.mixin.impl.power.custom.modify_model_color_self;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.eggohito.neo_apoli.api.misc.EntityCache;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

	@Shadow
	@Final
	private EntityRenderDispatcher entityRenderDispatcher;

	@Inject(method = "renderArmWithItem", at = @At("HEAD"))
	void onFirstPersonRender(AbstractClientPlayer player, float tickProgress, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {

		if (this.entityRenderDispatcher.getRenderer(player) instanceof EntityCache entityCache) {
			entityCache.neo_apoli$setEntity(player);
		}

	}

	@Inject(method = "renderArmWithItem", at = @At("TAIL"))
	void cleanUpAfter(AbstractClientPlayer player, float tickProgress, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {

		if (this.entityRenderDispatcher.getRenderer(player) instanceof EntityCache entityCache) {
			entityCache.neo_apoli$setEntity(null);
		}

	}

}
