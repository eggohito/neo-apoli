package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;

public record AttributeModifier(Holder<Attribute> attribute, net.minecraft.world.entity.ai.attributes.AttributeModifier modifier) {

	public static final Codec<AttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(AttributeModifier::attribute),
		net.minecraft.world.entity.ai.attributes.AttributeModifier.MAP_CODEC.forGetter(AttributeModifier::modifier)
	).apply(instance, AttributeModifier::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AttributeModifier> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE), AttributeModifier::attribute,
		net.minecraft.world.entity.ai.attributes.AttributeModifier.STREAM_CODEC, AttributeModifier::modifier,
		AttributeModifier::new
	);

}
