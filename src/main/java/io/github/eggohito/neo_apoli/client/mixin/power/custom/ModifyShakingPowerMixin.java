package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyShakingPower;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class ModifyShakingPowerMixin<S extends LivingEntityRenderState> {

	@ModifyReturnValue(method = "isShaking", at = @At("RETURN"))
	private boolean shakingProxy(boolean original, S state) {

		if (original) {
			return true;
		}

		else if (state instanceof EntityRenderCache renderCache) {
			return PowersComponent.hasPowerImpl(renderCache.neo_apoli$getEntity(), ModifyShakingPower.Impl.class, ModifyShakingPower.Impl::isActive);
		}

		else {
			return false;
		}

	}

}
