package io.github.eggohito.neo_apoli.hud;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.util.DynamicResourceLocation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record Sprite(ResourceLocation atlas, ResourceLocation id) {

	public static final MapCodec<Sprite> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DynamicResourceLocation.CODEC.fieldOf("atlas").forGetter(Sprite::atlas),
		DynamicResourceLocation.CODEC.fieldOf("id").forGetter(Sprite::id)
	).apply(instance, Sprite::new));

	public static final Codec<Sprite> CODEC = new MultiAlternativeCodec<>(MAP_CODEC.codec(), DynamicResourceLocation.CODEC.xmap(Sprite::new, Sprite::id));

	public static final StreamCodec<RegistryFriendlyByteBuf, Sprite> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, Sprite::atlas,
		ResourceLocation.STREAM_CODEC, Sprite::id,
		Sprite::new
	);

	public Sprite(ResourceLocation sprite) {
		this(OverlayHudElement.ATLAS_SHEET, sprite);
	}

}
