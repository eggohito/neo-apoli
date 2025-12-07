package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record AttributedAttributeModifier(Holder<Attribute> attribute, AttributeModifier modifier) {

	public static final Codec<AttributedAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Attribute.CODEC.fieldOf("attribute").forGetter(AttributedAttributeModifier::attribute),
		AttributeModifier.MAP_CODEC.forGetter(AttributedAttributeModifier::modifier)
	).apply(instance, AttributedAttributeModifier::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AttributedAttributeModifier> STREAM_CODEC = StreamCodec.composite(
		Attribute.STREAM_CODEC, AttributedAttributeModifier::attribute,
		AttributeModifier.STREAM_CODEC, AttributedAttributeModifier::modifier,
		AttributedAttributeModifier::new
	);

}
