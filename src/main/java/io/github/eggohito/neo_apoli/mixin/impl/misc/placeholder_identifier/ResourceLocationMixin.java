package io.github.eggohito.neo_apoli.mixin.impl.misc.placeholder_identifier;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.util.ResourceLocationUtil;
import net.minecraft.ResourceLocationException;
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
    private static String replacePlaceholderInNamespaces(String original, String namespace, String path) {

        try {
            return ResourceLocationUtil.replaceWithCurrent(original, ResourceLocation::getNamespace);
        }

        catch (ResourceLocationException e) {
            throw new ResourceLocationException(e.getMessage() + " in location: '" + namespace + ":" + path);
        }

    }

    @ModifyReturnValue(method = "assertValidPath", at = @At("RETURN"))
    private static String replacePlaceholderInPaths(String original, String namespace, String path) {

        try {
            return ResourceLocationUtil.replaceWithCurrent(original, ResourceLocation::getPath);
        }

        catch (ResourceLocationException e) {
            throw new ResourceLocationException(e.getMessage() + " in location: '" + namespace + ":" + path);
        }

    }

}
