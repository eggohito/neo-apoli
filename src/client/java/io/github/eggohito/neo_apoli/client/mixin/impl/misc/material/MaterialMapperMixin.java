package io.github.eggohito.neo_apoli.client.mixin.impl.misc.material;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MaterialMapper.class)
public abstract class MaterialMapperMixin {

	@Shadow
	public abstract String prefix();

	@WrapOperation(method = "apply", at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/resources/model/Material;"))
	private Material handleEmptyPrefixes(ResourceLocation atlasLocation, ResourceLocation texture, Operation<Material> original, ResourceLocation name) {

		texture = this.prefix().isEmpty()
			? name
			: texture;

		return original.call(atlasLocation, texture);

	}

}
