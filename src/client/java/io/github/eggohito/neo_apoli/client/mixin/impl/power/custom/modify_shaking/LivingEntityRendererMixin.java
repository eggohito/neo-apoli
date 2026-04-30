package io.github.eggohito.neo_apoli.client.mixin.impl.power.custom.modify_shaking;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.power.custom.ModifyModelShakingPower;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<S extends LivingEntityRenderState> {

	@ModifyReturnValue(method = "isShaking", at = @At("RETURN"))
	boolean letEntitiesShake(boolean original, S state) {
		Entity entity = state.neo_apoli$getEntity();
		return original
			|| Powers.hasInstances(entity, ModifyModelShakingPower.Instance.class, instance -> instance.isActive(entity));
	}

}
