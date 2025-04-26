package io.github.eggohito.neo_apoli.mixin.misc;

import io.github.eggohito.neo_apoli.power.PowerManager;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataPackContents.class)
public abstract class DataPackContentsMixin {

	@Inject(method = "applyPendingTagLoads", at = @At("HEAD"))
	private void neo_apoli$validatePowers(CallbackInfo ci) {
		PowerManager.validate((DataPackContents) (Object) this);
	}

}
