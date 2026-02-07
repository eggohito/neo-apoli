package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributedModifier(Holder<Attribute> attribute, AttributeModifier modifier) {

	public static final Codec<AttributedModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Attribute.CODEC.fieldOf("attribute").forGetter(AttributedModifier::attribute),
		AttributeModifier.MAP_CODEC.forGetter(AttributedModifier::modifier)
	).apply(instance, AttributedModifier::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AttributedModifier> STREAM_CODEC = StreamCodec.composite(
		Attribute.STREAM_CODEC, AttributedModifier::attribute,
		AttributeModifier.STREAM_CODEC, AttributedModifier::modifier,
		AttributedModifier::new
	);

}
