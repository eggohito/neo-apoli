package io.github.eggohito.neo_apoli.client.mixin.misc;

import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

	@Inject(method = "updateRenderState", at = @At("HEAD"))
	private void cacheEntity(T entity, S state, float tickProgress, CallbackInfo ci) {

		if (state instanceof EntityRenderCache renderCache) {
			renderCache.neo_apoli$setEntity(entity);
		}

	}

}
