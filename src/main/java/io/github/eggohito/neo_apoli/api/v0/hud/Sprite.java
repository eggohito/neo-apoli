package io.github.eggohito.neo_apoli.api.v0.hud;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record Sprite(ResourceLocation atlas, ResourceLocation id) {

	public static final Codec<Sprite> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("atlas").forGetter(Sprite::atlas),
		ResourceLocation.CODEC.fieldOf("id").forGetter(Sprite::id)
	).apply(instance, Sprite::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, Sprite> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, Sprite::atlas,
		ResourceLocation.STREAM_CODEC, Sprite::id,
		Sprite::new
	);

}
