package io.github.eggohito.neo_apoli.mixin.impl.misc.power_callbacks;

import io.github.eggohito.neo_apoli.api.power.Powers;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "baseTick", at = @At("TAIL"))
	void onTick(CallbackInfo ci) {

		Entity thisAsEntity = (Entity) (Object) this;
		Powers powers = Powers.getNullable(thisAsEntity);

		if (powers == null) {
			return;
		}

		for (var instance : powers.getAllInstances()) {

			if (instance.shouldTick(thisAsEntity)) {
				instance.onTick(thisAsEntity);
			}

		}

	}

}
