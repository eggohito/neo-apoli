package io.github.eggohito.neo_apoli.mixin.misc;

import io.github.eggohito.neo_apoli.power.PowerManager;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.SaveLoading;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SaveLoading.class)
public abstract class SaveLoadingMixin {

	@Inject(method = "method_42097", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/DataPackContents;applyPendingTagLoads()V"))
	private static <D, R> void onDataPacksLoaded(SaveLoading.SaveApplierFactory<D, R> saveApplierFactory, LifecycledResourceManager lifecycledResourceManager, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, SaveLoading.LoadContext<D> loadContext, DataPackContents dataPackContents, CallbackInfoReturnable<Object> cir) {
		PowerManager.validate(dataPackContents);
	}

}
