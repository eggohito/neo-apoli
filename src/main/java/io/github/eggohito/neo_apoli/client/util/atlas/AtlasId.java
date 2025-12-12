package io.github.eggohito.neo_apoli.client.util.atlas;

import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public record AtlasId(ResourceLocation sheet, ResourceLocation name, MaterialMapper mapper) {

	public Material getMaterial(ResourceLocation spriteId) {
		return mapper().apply(spriteId);
	}

	public TextureAtlasSprite getSprite(ResourceLocation spriteId) {
		return getMaterial(spriteId).sprite();
	}

	public static AtlasId prefixed(ResourceLocation sheet, ResourceLocation name, String prefix) {
		return new AtlasId(sheet, name, new MaterialMapper(sheet, prefix));
	}

	public static AtlasId prefixedWithPath(ResourceLocation name) {
		return prefixed(toSheet(name), name, name.getPath());
	}

	public static AtlasId of(ResourceLocation sheet, ResourceLocation name) {
		return prefixed(sheet, name, "");
	}

	public static AtlasId of(ResourceLocation name) {
		return of(toSheet(name), name);
	}

	private static ResourceLocation toSheet(ResourceLocation name) {
		return name.withPath(path -> "textures/atlas/" + path + ".png");
	}

}
