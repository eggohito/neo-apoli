package io.github.eggohito.neo_apoli.mixin.misc;

import io.github.eggohito.neo_apoli.power.PowerManager;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataPackContents.class)
public abstract class DataPackContentsMixin {

	@Inject(method = "applyPendingTagLoads", at = @At("TAIL"))
	private void applyCustomPendingTagsAndValidateCustomElements(CallbackInfo ci) {

		DataPackContents thisAsPackContents = (DataPackContents) (Object) this;

		PowerManager.validate(thisAsPackContents);
		PowerManager.applyPendingTags(thisAsPackContents);

	}

}
