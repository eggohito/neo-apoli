package io.github.eggohito.neo_apoli.client.util;

import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.hud.Sprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;

public class SpriteMaterial extends Material {

	public SpriteMaterial(Sprite sprite) {
		super(sprite.atlas(), sprite.id());
	}

	/**
	 * 	Use {@link #spriteAsResult()} to <b>safely</b> get the actual atlas sprite.
	 */
	@Deprecated
	@Override
	public TextureAtlasSprite sprite() {
		return super.sprite();
	}

	public DataResult<TextureAtlasSprite> spriteAsResult() {
		return TextureUtil.getSprite(this.atlasLocation(), this.texture());
	}

}
