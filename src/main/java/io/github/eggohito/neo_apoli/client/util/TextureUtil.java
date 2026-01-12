package io.github.eggohito.neo_apoli.client.util;

import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.client.mixin.access.AtlasSetAccessor;
import io.github.eggohito.neo_apoli.client.mixin.access.ModelManagerAccessor;
import io.github.eggohito.neo_apoli.client.mixin.access.TextureAtlasAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;

public class TextureUtil {

	public static DataResult<TextureAtlasSprite> getSprite(ResourceLocation atlasId, ResourceLocation spriteId) {
		return getAtlas(atlasId).flatMap(atlas -> getSpriteInternal(atlas, spriteId));
	}

	public static DataResult<TextureAtlas> getAtlas(ResourceLocation id) {

		Minecraft client = Minecraft.getInstance();
		ModelManager modelManager = client.getModelManager();

		AtlasSet atlasSet = ((ModelManagerAccessor) modelManager).getAtlases();
		AtlasSet.AtlasEntry entry = ((AtlasSetAccessor) atlasSet).getEntries().get(id);

		return entry == null
			? DataResult.error(() -> "Atlas \"" + id + "\" does not exist!")
			: DataResult.success(entry.atlas());

	}

	private static DataResult<TextureAtlasSprite> getSpriteInternal(TextureAtlas atlas, ResourceLocation spriteId) {

		TextureAtlasSprite sprite = atlas.getSprite(spriteId);
		DataResult<TextureAtlasSprite> result = DataResult.success(sprite);

		if (sprite == ((TextureAtlasAccessor) atlas).getMissingSprite()) {
			result = DataResult.error(() -> "Sprite \"" + spriteId + "\" does not exist in atlas \"" + atlas.location() + "\"!");
		}

		return result.setPartial(sprite);

	}

}
