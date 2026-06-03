package io.github.eggohito.neo_apoli.mixin.impl.event.reloadable_server_resources;

import io.github.eggohito.neo_apoli.api.event.ReloadableServerResourcesEvents;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {

	@Inject(method = "updateStaticRegistryTags", at = @At("TAIL"))
	private void afterLoadingRegistryTags(CallbackInfo ci) {
		ReloadableServerResourcesEvents.TAGS_UPDATED.invoker().afterLoad((ReloadableServerResources) (Object) this);
	}

}
