package io.github.eggohito.neo_apoli.client.mixin.impl.event.texture_atlas_registration;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.client.api.event.TextureAtlasRegistrationEvents;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

	@WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(Ljava/util/Map;Lnet/minecraft/client/renderer/texture/TextureManager;)Lnet/minecraft/client/resources/model/AtlasSet;"))
	private AtlasSet addCustomAtlases(Map<ResourceLocation, ResourceLocation> atlases, TextureManager textureManager, Operation<AtlasSet> original) {

		ImmutableMap.Builder<ResourceLocation, ResourceLocation> atlasesBuilder = ImmutableMap.builder();
		atlasesBuilder.putAll(atlases);

		TextureAtlasRegistrationEvents.SIMPLE.invoker().register(atlasId -> atlasesBuilder.put(atlasId.sheet(), atlasId.name()));
		return original.call(atlasesBuilder.build(), textureManager);

	}

}
