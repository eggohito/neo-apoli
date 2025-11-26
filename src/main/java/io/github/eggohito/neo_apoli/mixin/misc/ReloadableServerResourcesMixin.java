package io.github.eggohito.neo_apoli.mixin.misc;

import io.github.eggohito.neo_apoli.integration.ReloadableServerResourcesEvents;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {

	@Inject(method = "updateStaticRegistryTags", at = @At("HEAD"))
	private void beforeLoadingRegistryTags(CallbackInfo ci) {
		ReloadableServerResourcesEvents.UpdatingRegistryTagsEvents.BEFORE.invoker().onBeforeUpdate((ReloadableServerResources) (Object) this);
	}

	@Inject(method = "updateStaticRegistryTags", at = @At("TAIL"))
	private void afterLoadingRegistryTags(CallbackInfo ci) {
		ReloadableServerResourcesEvents.UpdatingRegistryTagsEvents.AFTER.invoker().onAfterUpdate((ReloadableServerResources) (Object) this);
	}

}
