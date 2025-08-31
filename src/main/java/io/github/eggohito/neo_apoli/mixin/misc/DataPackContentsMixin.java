package io.github.eggohito.neo_apoli.mixin.misc;

import io.github.eggohito.neo_apoli.integration.DataPackContentsEvents;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataPackContents.class)
public abstract class DataPackContentsMixin {

	@Inject(method = "applyPendingTagLoads", at = @At("HEAD"))
	private void beforePendingTagLoadEvent(CallbackInfo ci) {
		DataPackContentsEvents.PendingTagLoadEvents.BEFORE.invoker().onBeforeLoad((DataPackContents) (Object) this);
	}

	@Inject(method = "applyPendingTagLoads", at = @At("TAIL"))
	private void afterPendingTagLoadEvent(CallbackInfo ci) {
		DataPackContentsEvents.PendingTagLoadEvents.AFTER.invoker().onAfterLoad((DataPackContents) (Object) this);
	}

}
