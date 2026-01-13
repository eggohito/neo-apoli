package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin {

    @ModifyReturnValue(method = "validNamespaceChar", at = @At("RETURN"))
    private static boolean allowPlaceholderCharInNamespaces(boolean original, char namespaceChar) {
        return original
            || ResourceLocationUtil.isEnabledAndPlaceholder(namespaceChar);
    }

    @ModifyReturnValue(method = "validPathChar", at = @At("RETURN"))
    private static boolean allowPlaceholderCharInPaths(boolean original, char pathChar) {
        return original
            || ResourceLocationUtil.isEnabledAndPlaceholder(pathChar);
    }

    @ModifyReturnValue(method = "assertValidNamespace", at = @At("RETURN"))
    private static String replacePlaceholderInNamespaces(String original) {
        return ResourceLocationUtil.replaceWithCurrent(original, ResourceLocation::getNamespace);
    }

    @ModifyReturnValue(method = "assertValidPath", at = @At("RETURN"))
    private static String replacePlaceholderInPaths(String original) {
        return ResourceLocationUtil.replaceWithCurrent(original, ResourceLocation::getPath);
    }

}
