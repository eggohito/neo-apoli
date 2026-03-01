package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyModelShakingPower;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class ModifyShakingPowerMixin<S extends LivingEntityRenderState> {

	@ModifyReturnValue(method = "isShaking", at = @At("RETURN"))
	private boolean shakingProxy(boolean original, S state) {
		return original
			|| PowersComponent.hasInstances(state.neo_apoli$getEntity(), ModifyModelShakingPower.Instance.class, ModifyModelShakingPower.Instance::isActive);
	}

}
