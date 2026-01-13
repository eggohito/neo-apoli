package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ResourceLocation.class)
public interface ResourceLocationAccessor {

    @Invoker
    static ResourceLocation callCreateUntrusted(String namespace, String path) {
        throw new AssertionError();
    }

}
